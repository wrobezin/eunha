package io.github.wrobezin.eunha.crawler.entity;

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
    private String esId;
    private String contentDbId;
    private String url;
    private String contentType;
    private Boolean newPage;
    private Boolean updated;
    private Integer version;
    private LocalDateTime finishTime;

    public static final CrawlResult NO_RESULT = CrawlResult.builder()
            .esId("")
            .contentDbId("")
            .url("")
            .contentType("")
            .newPage(false)
            .updated(false)
            .version(-1)
            .build();
}
