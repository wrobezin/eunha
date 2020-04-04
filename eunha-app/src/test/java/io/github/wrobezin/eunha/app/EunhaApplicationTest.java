package io.github.wrobezin.eunha.app;

import io.github.wrobezin.eunha.crawler.PageCrawler;
import io.github.wrobezin.eunha.data.entity.document.OriginalDocument;
import io.github.wrobezin.eunha.data.entity.rule.CrawlRule;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import io.github.wrobezin.eunha.data.repository.mongo.OriginalDocumentMongoRepository;
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

    @Autowired
    private OriginalDocumentMongoRepository mongoRepository;

    @Test
    void testCrawl() {
        CrawlRule crawlRule = CrawlRule.builder().seedUrl("http://jiaren.org/category/d_human/")
                .expandable(true)
                .expandToOtherSite(false)
                .maxExpandDepth(0).build();
        CustomizedRule customizedRule = CustomizedRule.builder().crawlRule(crawlRule).build();
        crawler.crawl(customizedRule);
    }

    @Test
    void testMongo() {
        OriginalDocument document = OriginalDocument.builder()
                .id("1")
                .url("test")
                .title("test")
                .body("test")
                .version(0)
                .build();
        System.out.println(mongoRepository.save(document));
        document.setVersion(1);
        document.setId("2");
        System.out.println(mongoRepository.save(document));
        System.out.println(mongoRepository.findFirstByUrlOrderByVersionDesc("test"));
    }
}
