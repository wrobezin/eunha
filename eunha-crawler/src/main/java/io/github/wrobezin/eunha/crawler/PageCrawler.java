package io.github.wrobezin.eunha.crawler;

import io.github.wrobezin.eunha.crawler.data.DataOpertorWraper;
import io.github.wrobezin.eunha.crawler.entity.CrawlResult;
import io.github.wrobezin.eunha.crawler.entity.DownloadResult;
import io.github.wrobezin.eunha.crawler.entity.HyperLinkToDownload;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.crawler.parser.ParserRouter;
import io.github.wrobezin.eunha.crawler.queue.HyperLinkExpandQueue;
import io.github.wrobezin.eunha.crawler.queue.MemoryHyperLinkExpandQueue;
import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.eunha.data.entity.rule.CrawlRule;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import io.github.wrobezin.framework.utils.http.HttpUrlUtils;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private HyperLinkExpandQueue queue = new MemoryHyperLinkExpandQueue();

    private final DataOpertorWraper dataOpertorWraper;

    private final ParserRouter parserRouter;

    private final Integer SLEEP_TIME = 1000;

    public PageCrawler(ParserRouter parserRouter, DataOpertorWraper dataOpertorWraper) {
        this.parserRouter = parserRouter;
        this.dataOpertorWraper = dataOpertorWraper;
    }

    @Override
    public List<CrawlResult> crawl(CustomizedRule customizedRule) {
        CrawlRule crawlRule = customizedRule.getCrawlRule();
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
                log.info("下载链接：{} {}", linkToDownload.getLink().getAnchorText(), linkToDownload.getLink().getUrl());
                // 下载页面
                DownloadResult downloadResult = downloadPage(linkToDownload.getLink());
                // 调用解析器解析页面
                ParseResult parseResult = parserRouter.parse(downloadResult);
                // 处理解析结果并将最终爬取结果添加到爬取结果列表
                crawlResults.add(handleParseResult(parseResult));
                // URL扩展
                if (crawlRule.getExpandable()) {
                    parseResult.getLinks().stream()
                            .filter(url -> crawlRule.getExpandToOtherSite() || HttpUrlUtils.hasSameHost(url.getUrl(), linkToDownload.getLink().getUrl()))
                            .map(expandedLink -> new HyperLinkToDownload(expandedLink, linkToDownload, parseResult, customizedRule.getInterestRule()))
                            .forEach(queue::offer);
                }
                sleep();
            } catch (IOException e) {
                log.error("下载{}出错：{}", linkToDownload, e);
            }
        }
        return crawlResults;
    }

    private DownloadResult downloadPage(HyperLink link) throws IOException {
        UrlInfo urlInfo = HttpUrlUtils.parseUrl(link.getUrl());
        Response response = get(link.getUrl());
        return new DownloadResult(urlInfo, response);
    }

    /**
     * 处理解析结果
     *
     * @param parseResult 解析结果
     * @return 最终爬取结果
     */
    private CrawlResult handleParseResult(ParseResult parseResult) {
        return dataOpertorWraper.savePageData(parseResult);
    }

    private void sleep() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------网络相关------------------------------------------

    private final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    protected final Response get(String url) throws IOException {
        return HTTP_CLIENT.newCall(new Request.Builder()
                .url(url)
                .get()
                .build()).execute();
    }

    protected final Response post(String url, Map<String, String> formParam) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        formParam.forEach(formBuilder::add);
        return HTTP_CLIENT.newCall(new Request.Builder()
                .url(url)
                .post(formBuilder.build())
                .build()).execute();
    }

    protected final Response postJson(String url, String json) throws IOException {
        return HTTP_CLIENT.newCall(new Request.Builder()
                .url(url)
                .post(RequestBody.create(json, MediaType.parse("application/json; charset=utf-8")))
                .build()).execute();
    }
}
