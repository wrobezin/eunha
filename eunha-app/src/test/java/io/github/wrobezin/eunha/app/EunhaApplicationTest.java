package io.github.wrobezin.eunha.app;

import io.github.wrobezin.eunha.app.diff.DifferenceUtils;
import io.github.wrobezin.eunha.app.service.PageService;
import io.github.wrobezin.eunha.app.service.RuleService;
import io.github.wrobezin.eunha.crawler.Estimater;
import io.github.wrobezin.eunha.crawler.PageCrawler;
import io.github.wrobezin.eunha.crawler.PageOpertor;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.data.entity.document.Page;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import io.github.wrobezin.eunha.data.entity.rule.InterestRule;
import io.github.wrobezin.eunha.data.entity.rule.SingleInterestRuleItem;
import io.github.wrobezin.eunha.data.enums.RuleItemJudgeTypeEnum;
import io.github.wrobezin.eunha.data.repository.elasticsearch.PageElasticsearchRepository;
import io.github.wrobezin.eunha.data.repository.mongo.CompatibilityScoreMongoRepository;
import io.github.wrobezin.eunha.data.repository.mongo.CustomizedRuleMongoRepository;
import io.github.wrobezin.eunha.push.mail.MailService;
import io.github.wrobezin.framework.utils.http.HttpUrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
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
    private PageOpertor pageOpertor;

    @Autowired
    private Estimater estimate;

    @Autowired
    private CompatibilityScoreMongoRepository compatibilityScoreRepository;

    @Autowired
    private PageElasticsearchRepository pageEsRepository;

    @Autowired
    private PageService pageService;

    @Autowired
    private MailService mailService;

    @Autowired
    private CustomizedRuleMongoRepository ruleRepository;

    @Test
    void testCrawl() {
//        CustomizedRule customizedRule = ruleService.findAll().get(0);
//        customizedRule.getCrawlRule().setMaxExpandDepth(1);
        CustomizedRule customizedRule = ruleRepository.findByName("煎蛋首页");
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
    void testAdd() {
        ParseResult parseResult = ParseResult.builder()
                .urlInfo(HttpUrlUtils.parseUrl("http://fuck.you/sb/elasticsearch/"))
                .links(new ArrayList<>())
                .body("<div>链接：<a herf=\"fuck\">阳光普照<a>大地回春，春天到了，花开了</div>")
                .title("春天的风光")
                .build();
        System.out.println(pageOpertor.savePageData(parseResult));
    }

    @Test
    void testSearch() {
        pageService.searchByKeywords("马 云", 0, 1)
                .forEach(page -> {
                    System.out.println(page.getTitle());
                    System.out.println(page.getUrl());
                    System.out.println(page.getSummary());
//                    System.out.println(page.getBody());
                    System.out.println("----------------------------------");
                });
    }

    @Test
    void testMatch() {
        System.out.println(compatibilityScoreRepository.countByRuleIdAndValueGreaterThanEqual("6c787e0c-1d56-4be9-a8a3-7e51d63b9dda", 1.0));
        compatibilityScoreRepository.findByRuleIdAndValueGreaterThanEqual("6c787e0c-1d56-4be9-a8a3-7e51d63b9dda", 1.0, PageRequest.of(0, 100))
                .forEach(score -> {
                    System.out.println(score.getUrl());
                    String url = score.getUrl();
                    Page page = pageEsRepository.findByUrl(url);
                    System.out.println(page.getUrl());
                    System.out.println("-------------------------------");
                });
    }

    @Test
    void testMail() {
        mailService.sendMail("liangsiyuan@whutosa.com", "test", "测试<br><hr><ur><li>1</li><li>2</li><li>3</li></ur>");
    }

    @Test
    void temp() {
        Page p1 = Page.builder()
                .title("")
                .body("更多死亡：研究显示羟氯喹于新冠治疗无益\n" +
                        "超1/3美国成人服用阿片类药物，数百万人滥用\n" +
                        "“如何让你的老公在封闭隔离期间开心”的海报遭谴责\n" +
                        "漫画行业能挺过新冠疫情吗？\n" +
                        "受疫情影响，纽约州将允许情侣线上结婚\n" +
                        "美国疾控中心下发了被新冠病毒污染的失效试剂盒")
                .build();
        Page p2 = Page.builder()
                .title("")
                .body("“如何让你的老公在封闭隔离期间开心”的海报遭谴责\n" +
                        "漫画行业能挺过新冠疫情吗？\n" +
                        "受疫情影响，纽约州将允许情侣线上结婚\n" +
                        "美国疾控中心下发了被新冠病毒污染的失效试剂盒\n" +
                        "关于克里奥佩特拉的5个趣闻\n" +
                        "在玩耍这件事上，狗子和马儿有共同语言")
                .build();
        DifferenceUtils.diff(p1, p2).forEach(System.out::println);
    }
}
