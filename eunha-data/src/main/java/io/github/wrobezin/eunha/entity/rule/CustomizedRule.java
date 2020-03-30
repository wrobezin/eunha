package io.github.wrobezin.eunha.entity.rule;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户自定义规则
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/30 13:50
 */
@Data
@Builder
@NoArgsConstructor
public class CustomizedRule {
    private String id;
    /** 规则名 */
    private String name;
    /** 抓取规则 */
    private CrawlRule crawlRule;
    /** 兴趣规则 */
    private InterestRule interestRule;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
