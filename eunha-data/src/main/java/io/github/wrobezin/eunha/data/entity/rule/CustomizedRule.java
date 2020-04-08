package io.github.wrobezin.eunha.data.entity.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@AllArgsConstructor
@Document(collection = "rule")
public class CustomizedRule {
    @Id
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
