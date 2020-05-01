package io.github.wrobezin.eunha.app.task;

import io.github.wrobezin.eunha.app.service.RuleService;
import io.github.wrobezin.eunha.crawler.PageCrawler;
import io.github.wrobezin.eunha.crawler.entity.CrawlResult;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import io.github.wrobezin.eunha.push.PushHub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/10 16:57
 */
@Component
@Slf4j
public class CrawlTask {
    private final PageCrawler crawler;
    private final RuleService ruleService;
    private final PushHub pushHub;

    public CrawlTask(PageCrawler crawler, RuleService ruleService, PushHub pushHub) {
        this.crawler = crawler;
        this.ruleService = ruleService;
        this.pushHub = pushHub;
    }

//    @Scheduled(cron = "00 25 16 * * ?")
    @Async
    public void run() {
        List<CustomizedRule> rules = ruleService.findRules();
        log.info("从数据库中获取{}条规则", rules.size());
        rules.forEach(rule -> {
            log.info("开始抓取规则{}-{}", rule.getName(), rule.getId());
            List<CrawlResult> resultList = crawler.crawl(rule);
            log.info("规则{}-{}抓取完毕", rule.getName(), rule.getId());
            pushHub.push(resultList, rule);
        });
    }
}
