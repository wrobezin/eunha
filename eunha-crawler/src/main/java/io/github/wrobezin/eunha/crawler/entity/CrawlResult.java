package io.github.wrobezin.eunha.crawler.entity;

import io.github.wrobezin.eunha.data.entity.document.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 爬取结果
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/30 15:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlResult {
    private String url;
    private Boolean newPage;
    private Boolean updated;
    private Integer version;
    private LocalDateTime finishTime;
    private Page pageInDb;
    private Boolean isRuleMatched;

    public static final CrawlResult BLANK = CrawlResult.builder()
            .url("")
            .newPage(false)
            .updated(false)
            .version(-1)
            .isRuleMatched(false)
            .build();
}
