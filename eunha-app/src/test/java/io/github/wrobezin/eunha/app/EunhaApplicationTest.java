package io.github.wrobezin.eunha.app;

import io.github.wrobezin.eunha.app.service.RuleService;
import io.github.wrobezin.eunha.crawler.DataOpertor;
import io.github.wrobezin.eunha.crawler.Estimater;
import io.github.wrobezin.eunha.crawler.PageCrawler;
import io.github.wrobezin.eunha.data.entity.document.Page;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import io.github.wrobezin.eunha.data.entity.rule.InterestRule;
import io.github.wrobezin.eunha.data.entity.rule.SingleInterestRuleItem;
import io.github.wrobezin.eunha.data.enums.RuleItemJudgeTypeEnum;
import io.github.wrobezin.eunha.data.repository.elasticsearch.PageElasticsearchRepository;
import io.github.wrobezin.eunha.data.repository.mongo.CompatibilityScoreMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;

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
    private RuleService ruleService;

    @Autowired
    private PageElasticsearchRepository pageRepository;

    @Autowired
    private DataOpertor dataOpertor;

    @Autowired
    private Estimater estimate;

    @Autowired
    private CompatibilityScoreMongoRepository compatibilityScoreRepository;

    @Autowired
    private PageElasticsearchRepository pageEsRepository;

    @Test
    void testCrawl() {
        CustomizedRule customizedRule = ruleService.findAll().get(0);
        customizedRule.getCrawlRule().setMaxExpandDepth(1);
        crawler.crawl(customizedRule);
    }

    @Test
    void testEstimater() {
        InterestRule rule1 = InterestRule.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "A")
                .and(InterestRule.group(Arrays.asList(
                        SingleInterestRuleItem.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "B"),
                        SingleInterestRuleItem.or(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "C"))));
        Page document11 = Page.builder()
                .title("A")
                .body("")
                .build();
        Page document12 = Page.builder()
                .title("AB")
                .body("")
                .build();
        Page document13 = Page.builder()
                .title("C")
                .body("")
                .build();
        System.out.println(rule1);
        System.out.println(estimate.estimate(document11, rule1));
        System.out.println(estimate.estimate(document12, rule1));
        System.out.println(estimate.estimate(document13, rule1));
        System.out.println("---------------------------------------");

        InterestRule rule2 = InterestRule.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "A")
                .or(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "B")
                .and(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "C");
        Page document21 = Page.builder()
                .title("A")
                .body("")
                .build();
        Page document22 = Page.builder()
                .title("AB")
                .body("")
                .build();
        Page document23 = Page.builder()
                .title("C")
                .body("")
                .build();
        System.out.println(rule2);
        System.out.println(estimate.estimate(document21, rule2));
        System.out.println(estimate.estimate(document22, rule2));
        System.out.println(estimate.estimate(document23, rule2));
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
        Page document31 = Page.builder()
                .title("BDE")
                .body("")
                .build();
        Page document32 = Page.builder()
                .title("ACDFGI")
                .body("")
                .build();
        Page document33 = Page.builder()
                .title("FD")
                .body("")
                .build();
        Page document34 = Page.builder()
                .title("IB")
                .body("")
                .build();
        Page document35 = Page.builder()
                .title("I")
                .body("")
                .build();
        Page document36 = Page.builder()
                .title("GFC")
                .body("")
                .build();
        System.out.println(rule3);
        System.out.println(estimate.estimate(document31, rule3));
        System.out.println(estimate.estimate(document32, rule3));
        System.out.println(estimate.estimate(document33, rule3));
        System.out.println(estimate.estimate(document34, rule3));
        System.out.println(estimate.estimate(document35, rule3));
        System.out.println(estimate.estimate(document36, rule3));
        System.out.println("---------------------------------------");
    }

    @Test
    void testSearch() {
        dataOpertor.searchByKerword(Arrays.asList("心语", "阳光"), 0, 100)
                .forEach(obj -> {
                    System.out.println(obj.getTitle());
                    System.out.println(obj.getUrl());
//                        System.out.println(((Page) obj).getBody());
                    System.out.println("----------------------------------");
                });
    }

    @Test
    void testMatch() {
        System.out.println(compatibilityScoreRepository.countByRuleIdAndValueGreaterThanEqual("6c787e0c-1d56-4be9-a8a3-7e51d63b9dda", 1.0));
        compatibilityScoreRepository.findByRuleIdAndValueGreaterThanEqual("6c787e0c-1d56-4be9-a8a3-7e51d63b9dda", 1.0, PageRequest.of(0, 100))
                .forEach(score -> {
                    System.out.println(score.getUrl());
                    String escape = QueryParser.escape(score.getUrl());
                    System.out.println(escape);
                    Page page = pageEsRepository.findByUrl(escape);
                    System.out.println(page.getUrl());
                    System.out.println("-------------------------------");
                });
    }
}
