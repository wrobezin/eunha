package io.github.wrobezin.eunha.app.task;

import io.github.wrobezin.eunha.app.service.RuleService;
import io.github.wrobezin.eunha.crawler.PageCrawler;
import io.github.wrobezin.eunha.crawler.entity.CrawlResult;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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

    public CrawlTask(PageCrawler crawler, RuleService ruleService) {
        this.crawler = crawler;
        this.ruleService = ruleService;
    }

//    @Scheduled(cron = "0 0 0/3 * * ?")
    @Scheduled(cron = "0 0 * * * ?")
    public void run() {
        List<CustomizedRule> rules = ruleService.findAll();
        log.info("从数据库中获取{}条规则", rules.size());
        rules.forEach(rule -> {
            log.info("开始抓取规则{}-{}", rule.getName(), rule.getId());
            List<CrawlResult> resultList = crawler.crawl(rule);
            log.info("规则{}-{}抓取完毕，抓取结果{}", rule.getName(), rule.getId(), resultList);
        });
    }
}