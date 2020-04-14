package io.github.wrobezin.eunha.app;

import io.github.wrobezin.eunha.app.service.RuleService;
import io.github.wrobezin.eunha.crawler.PageCrawler;
import io.github.wrobezin.eunha.crawler.data.DataOpertorWraper;
import io.github.wrobezin.eunha.crawler.estimate.OringinalDocumentEstimater;
import io.github.wrobezin.eunha.data.entity.document.OriginalDocument;
import io.github.wrobezin.eunha.data.entity.document.Page;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import io.github.wrobezin.eunha.data.entity.rule.InterestRule;
import io.github.wrobezin.eunha.data.entity.rule.SingleInterestRuleItem;
import io.github.wrobezin.eunha.data.enums.RuleItemJudgeTypeEnum;
import io.github.wrobezin.eunha.data.repository.elasticsearch.PageElasticsearchRepository;
import io.github.wrobezin.eunha.data.repository.mongo.OriginalDocumentMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/3/31 23:02
 */
@SpringBootTest
@Slf4j
class EunhaApplicationTest {
    @Autowired
    private PageCrawler crawler;

    @Autowired
    private OringinalDocumentEstimater documentEstimater;

    @Autowired
    private OriginalDocumentMongoRepository mongoRepository;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private PageElasticsearchRepository pageRepository;

    @Autowired
    private DataOpertorWraper dataOpertorWraper;

    @Test
    void testCrawl() {
        CustomizedRule customizedRule = ruleService.findAll().get(0);
        customizedRule.getCrawlRule().setMaxExpandDepth(2);
        System.out.println(crawler.crawl(customizedRule));
    }

    @Test
    void testMongo() {
        mongoRepository.deleteAllByUrl("test");
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
        mongoRepository.deleteAllByUrl("test");
    }

    @Test
    void testEstimater() {
        InterestRule rule1 = InterestRule.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "A")
                .and(InterestRule.group(Arrays.asList(
                        SingleInterestRuleItem.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "B"),
                        SingleInterestRuleItem.or(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "C"))));
        OriginalDocument document11 = OriginalDocument.builder()
                .title("A")
                .body("")
                .build();
        OriginalDocument document12 = OriginalDocument.builder()
                .title("AB")
                .body("")
                .build();
        OriginalDocument document13 = OriginalDocument.builder()
                .title("C")
                .body("")
                .build();
        System.out.println(rule1);
        System.out.println(documentEstimater.estimate(document11, rule1.getInterestRules()));
        System.out.println(documentEstimater.estimate(document12, rule1.getInterestRules()));
        System.out.println(documentEstimater.estimate(document13, rule1.getInterestRules()));
        System.out.println("---------------------------------------");

        InterestRule rule2 = InterestRule.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "A")
                .or(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "B")
                .and(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "C");
        OriginalDocument document21 = OriginalDocument.builder()
                .title("A")
                .body("")
                .build();
        OriginalDocument document22 = OriginalDocument.builder()
                .title("AB")
                .body("")
                .build();
        OriginalDocument document23 = OriginalDocument.builder()
                .title("C")
                .body("")
                .build();
        System.out.println(rule2);
        System.out.println(documentEstimater.estimate(document21, rule2.getInterestRules()));
        System.out.println(documentEstimater.estimate(document22, rule2.getInterestRules()));
        System.out.println(documentEstimater.estimate(document23, rule2.getInterestRules()));
        System.out.println("---------------------------------------");

        InterestRule rule3 = InterestRule.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "A")
                .or(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "B")
                .and(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "C")
                .or(InterestRule.group(Arrays.asList(
                        SingleInterestRuleItem.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "D"),
                        InterestRule.andGroup(Arrays.asList(
                                SingleInterestRuleItem.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "E"),
                                SingleInterestRuleItem.and(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "F"))),
                        InterestRule.andGroup(Arrays.asList(
                                SingleInterestRuleItem.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "G"),
                                InterestRule.orGroup(Arrays.asList(
                                        SingleInterestRuleItem.first(RuleItemJudgeTypeEnum.TITLE_NOT_CONTAIN, "H"),
                                        SingleInterestRuleItem.and(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "I"))))))));
        OriginalDocument document31 = OriginalDocument.builder()
                .title("BDE")
                .body("")
                .build();
        OriginalDocument document32 = OriginalDocument.builder()
                .title("ACDFGI")
                .body("")
                .build();
        OriginalDocument document33 = OriginalDocument.builder()
                .title("FD")
                .body("")
                .build();
        OriginalDocument document34 = OriginalDocument.builder()
                .title("IB")
                .body("")
                .build();
        OriginalDocument document35 = OriginalDocument.builder()
                .title("I")
                .body("")
                .build();
        OriginalDocument document36 = OriginalDocument.builder()
                .title("GFC")
                .body("")
                .build();
        System.out.println(rule3);
        System.out.println(documentEstimater.estimate(document31, rule3.getInterestRules()));
        System.out.println(documentEstimater.estimate(document32, rule3.getInterestRules()));
        System.out.println(documentEstimater.estimate(document33, rule3.getInterestRules()));
        System.out.println(documentEstimater.estimate(document34, rule3.getInterestRules()));
        System.out.println(documentEstimater.estimate(document35, rule3.getInterestRules()));
        System.out.println(documentEstimater.estimate(document36, rule3.getInterestRules()));
        System.out.println("---------------------------------------");
    }

    @Test
    void temp() {
        String ruleId = ruleService.findAll().get(0).getId();
        List<Page> allPageSuite = ruleService.getAllPageMatching(ruleId);
        System.out.println(allPageSuite.size());
    }

    @Test
    void temp1() {
        dataOpertorWraper.searchContent("心语", 0, 100)
                .forEach(obj -> {
                    if (obj instanceof OriginalDocument) {
                        System.out.println(((OriginalDocument) obj).getTitle());
                        System.out.println(((OriginalDocument) obj).getUrl());
//                        System.out.println(((OriginalDocument) obj).getBody());
                        System.out.println("----------------------------------");
                    }
                });
    }
}
