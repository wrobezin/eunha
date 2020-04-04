package io.github.wrobezin.eunha.crawler;

import io.github.wrobezin.eunha.crawler.entity.CrawlResult;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;

import java.util.List;


/**
 * 爬虫接口
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/30 14:57
 */
public interface Crawler {
    /**
     * 爬取
     *
     * @param customizedRule 用户自定义规则
     * @return 爬取结果
     */
    List<CrawlResult> crawl(CustomizedRule customizedRule);
}
