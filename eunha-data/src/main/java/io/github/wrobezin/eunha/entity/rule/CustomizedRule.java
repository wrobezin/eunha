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
    private CrawlRule crawlRule;
    private InterestRule interestRule;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
