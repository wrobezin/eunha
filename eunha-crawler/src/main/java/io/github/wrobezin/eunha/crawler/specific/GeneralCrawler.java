package io.github.wrobezin.eunha.crawler.specific;

import io.github.wrobezin.eunha.crawler.base.AbstractPageCrawler;
import io.github.wrobezin.eunha.crawler.base.BaseHtmlParser;
import io.github.wrobezin.eunha.crawler.base.entity.CrawlResult;
import io.github.wrobezin.eunha.crawler.base.entity.DownloadResult;
import io.github.wrobezin.eunha.crawler.base.entity.ParseResult;
import io.github.wrobezin.eunha.crawler.base.queue.HyperLinkExpandQueue;
import io.github.wrobezin.eunha.crawler.base.queue.MemoryHyperLinkExpandQueue;
import org.springframework.stereotype.Component;

/**
 * 通用页面爬虫
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/31 19:50
 */
@Component
public class GeneralCrawler extends AbstractPageCrawler<String> {
    private static final String CRAWLER_NAME = "通用页面爬虫";

    // TODO Spring注入
    private static final HyperLinkExpandQueue QUEUE = new MemoryHyperLinkExpandQueue();

    private final BaseHtmlParser parser;

    protected GeneralCrawler(BaseHtmlParser parser) {
        super(QUEUE);
        this.parser = parser;
    }

    @Override
    protected String crawlerName() {
        return CRAWLER_NAME;
    }

    @Override
    protected ParseResult<String> parse(DownloadResult downloadResult) {
        return parser.parse(downloadResult);
    }

    @Override
    protected CrawlResult handleParseResult(ParseResult<String> parseResult) {
        // TODO 完成保存页面内容等工作并返回结果
        return null;
    }
}
