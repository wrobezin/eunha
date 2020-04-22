package io.github.wrobezin.eunha.crawler;

import io.github.wrobezin.eunha.crawler.entity.CrawlResult;
import io.github.wrobezin.eunha.crawler.entity.DownloadResult;
import io.github.wrobezin.eunha.crawler.entity.HyperLinkToDownload;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.crawler.queue.HyperLinkExpandQueue;
import io.github.wrobezin.eunha.crawler.queue.MemoryHyperLinkExpandQueue;
import io.github.wrobezin.eunha.data.entity.document.CompatibilityScore;
import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.eunha.data.entity.rule.CrawlRule;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import io.github.wrobezin.eunha.data.entity.rule.InterestRule;
import io.github.wrobezin.eunha.data.repository.mongo.CompatibilityScoreMongoRepository;
import io.github.wrobezin.framework.utils.http.HttpUrlUtils;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 网页爬虫类，抓取HTML内容
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/30 15:49
 */
@Slf4j
@Component
public class PageCrawler implements Crawler {
    // ----------------------------------------工作逻辑相关----------------------------------------
    private HyperLinkExpandQueue queue = new MemoryHyperLinkExpandQueue((l1, l2) -> l2.getScore().compareTo(l1.getScore()));
    private final Integer SLEEP_TIME = 1000;

    private final Estimater estimater;
    private final CompatibilityScoreMongoRepository compatibilityRepository;
    private final GeneralHtmlParser parser;
    private final PageOpertor pageOpertor;
    private final HttpClient httpClient;

    public PageCrawler(Estimater estimater, CompatibilityScoreMongoRepository compatibilityRepository, GeneralHtmlParser parser, PageOpertor pageOpertor, HttpClient httpClient) {
        this.estimater = estimater;
        this.compatibilityRepository = compatibilityRepository;
        this.parser = parser;
        this.pageOpertor = pageOpertor;
        this.httpClient = httpClient;
    }

    @Override
    public List<CrawlResult> crawl(CustomizedRule customizedRule) {
        CrawlRule crawlRule = customizedRule.getCrawlRule();
        InterestRule interestRule = customizedRule.getInterestRule();
        HashSet<String> visited = new HashSet<>(64);
        // 将种子URL加入队列
        queue.offer(new HyperLinkToDownload(crawlRule.getSeedUrl()));
        List<CrawlResult> crawlResults = new ArrayList<>();
        while (!queue.isEmpty()) {
            HyperLinkToDownload linkToDownload = queue.poll();
            Integer depth = linkToDownload.getDepth();
            if (depth > crawlRule.getMaxExpandDepth()) {
                continue;
            }
            try {
                // 使用带query而不带fragment的URL进行去重
                String urlToDonwload = HttpUrlUtils.parseUrl(linkToDownload.getLink().getUrl()).getUrlWithQuery();
                if (visited.contains(urlToDonwload)) {
                    continue;
                }
                log.info("下载链接：{} {}", linkToDownload.getLink().getAnchorText(), urlToDonwload);
                // 下载页面
                DownloadResult downloadResult = downloadPage(linkToDownload.getLink());
                visited.add(urlToDonwload);
                // 调用解析器解析页面
                ParseResult parseResult = parser.parse(downloadResult);
                // 处理解析结果并将最终爬取结果添加到爬取结果列表
                CrawlResult crawlResult = handleParseResult(parseResult);
                crawlResults.add(crawlResult);
                // TODO 目前无论页面是否有更新，都重新评估匹配度，因为规则也可能发生过改变。之后需要进行优化，页面和规则都不变时，直接使用历史数据。
                // 评估页面与兴趣规则之间的匹配度
                double compatibility = estimater.estimate(crawlResult.getPageInDb(), interestRule);
                log.info("{} : {}", crawlResult.getPageInDb().getTitle(), compatibility);
                crawlResult.setIsRuleMatched(compatibility == Estimater.TRUE);
                // 保存最新匹配度
                compatibilityRepository.save(new CompatibilityScore(urlToDonwload, customizedRule.getId(), compatibility));
                // URL扩展
                if (crawlRule.getExpandable()) {
                    parseResult.getLinks().stream()
                            .filter(url -> crawlRule.getExpandToOtherSite() || HttpUrlUtils.hasSameHost(url.getUrl(), linkToDownload.getLink().getUrl()))
                            .map(expandedLink -> new HyperLinkToDownload(expandedLink, linkToDownload, parseResult, interestRule))
                            .forEach(expandedLink -> {
                                expandedLink.setScore(estimater.estimate(compatibility, expandedLink, customizedRule));
                                queue.offer(expandedLink);
                            });
                }
                sleep();
            } catch (IOException e) {
                log.error("下载{}出错：{}", linkToDownload, e);
            }
        }
        visited.clear();
        return crawlResults;
    }

    private DownloadResult downloadPage(HyperLink link) throws IOException {
        UrlInfo urlInfo = HttpUrlUtils.parseUrl(link.getUrl());
        Response response = httpClient.get(link.getUrl());
        return new DownloadResult(urlInfo, response);
    }

    /**
     * 处理解析结果
     *
     * @param parseResult 解析结果
     * @return 最终爬取结果
     */
    private CrawlResult handleParseResult(ParseResult parseResult) {
        return pageOpertor.savePageData(parseResult);
    }

    private void sleep() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
