package io.github.wrobezin.eunha.app;

import io.github.wrobezin.eunha.crawler.PageCrawler;
import io.github.wrobezin.eunha.data.entity.rule.CrawlRule;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/3/31 23:02
 */
@SpringBootTest
class EunhaApplicationTest {
    @Autowired
    private PageCrawler crawler;

    @Test
    void test() {
        CrawlRule crawlRule = CrawlRule.builder().seedUrl("http://jiaren.org/category/d_human/")
                .expandable(true)
                .expandToOtherSite(false)
                .maxExpandDepth(0).build();
        CustomizedRule customizedRule = CustomizedRule.builder().crawlRule(crawlRule).build();
        crawler.crawl(customizedRule);
    }
}
