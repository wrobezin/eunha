package io.github.wrobezin.eunha.crawler.base;

import io.github.wrobezin.eunha.crawler.base.entity.CrawlResult;
import io.github.wrobezin.eunha.crawler.base.entity.DownloadResult;
import io.github.wrobezin.eunha.crawler.base.entity.HyperLinkToDownload;
import io.github.wrobezin.eunha.crawler.base.entity.ParseResult;
import io.github.wrobezin.eunha.crawler.base.queue.HyperLinkExpandQueue;
import io.github.wrobezin.eunha.entity.document.HyperLink;
import io.github.wrobezin.eunha.entity.rule.CrawlRule;
import io.github.wrobezin.eunha.entity.rule.CustomizedRule;
import io.github.wrobezin.framework.utils.http.HttpUrlUtils;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 抽象爬虫类
 *
 * @param <T> 解析结果类型
 * @author yuan
 * @version 1.0
 * @date 2020/3/30 15:49
 */
@Slf4j
public abstract class AbstractPageCrawler<T> implements Crawler {
    // ----------------------------------------工作逻辑相关----------------------------------------

    private HyperLinkExpandQueue queue;

    protected AbstractPageCrawler(HyperLinkExpandQueue queue) {
        this.queue = queue;
    }

    /**
     * 获取爬虫名
     *
     * @return 爬虫名
     */
    protected abstract String crawlerName();

    @Override
    public List<CrawlResult> crawl(CustomizedRule customizedRule) {
        CrawlRule crawlRule = customizedRule.getCrawlRule();
        // 将种子URL加入队列
        queue.offer(new HyperLinkToDownload(0, new HyperLink(crawlRule.getSeedUrl())));
        List<CrawlResult> crawlResults = new ArrayList<>();
        while (!queue.isEmpty()) {
            HyperLinkToDownload link = queue.poll();
            Integer depth = link.getDepth();
            if (depth > crawlRule.getMaxExpandDepth()) {
                continue;
            }
            try {
                // 下载页面
                DownloadResult downloadResult = downloadPage(link.getLink());
                // 解析页面
                ParseResult<T> parseResult = parse(downloadResult);
                // 处理解析结果并将最终爬取结果添加到爬取结果列表
                crawlResults.add(handleParseResult(parseResult));
                // URL扩展
                if (crawlRule.getExpandable()) {
                    parseResult.getLinks().stream()
                            .filter(url -> crawlRule.getExpandToOtherSite() || HttpUrlUtils.hasSameHost(url.getUrl(), link.getLink().getUrl()))
                            .map(expandedLink -> new HyperLinkToDownload(depth + 1, expandedLink))
                            .forEach(queue::offer);
                }
            } catch (IOException e) {
                log.error("{}下载{}出错：{}", crawlerName(), link, e);
            }
        }
        return crawlResults;
    }

    protected DownloadResult downloadPage(HyperLink link) throws IOException {
        UrlInfo urlInfo = HttpUrlUtils.parseUrl(link.getUrl());
        Response response = get(link.getUrl());
        return new DownloadResult(urlInfo, response);
    }

    /**
     * 对下载结果进行解析
     *
     * @param downloadResult 下载结果
     * @return 解析结果
     */
    protected abstract ParseResult<T> parse(DownloadResult downloadResult);

    /**
     * 处理解析结果
     *
     * @param parseResult 解析结果
     * @return 最终爬取结果
     */
    protected abstract CrawlResult handleParseResult(ParseResult<T> parseResult);

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
