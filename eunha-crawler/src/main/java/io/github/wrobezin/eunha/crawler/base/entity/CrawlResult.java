package io.github.wrobezin.eunha.crawler.base.entity;

import io.github.wrobezin.eunha.enums.PageContentTypeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
public class CrawlResult {
    private String searchEnginePageId;
    private String databasePageId;
    private String url;
    private PageContentTypeEnum contentType;
    private Boolean newPage;
    private Boolean updated;
    private Integer version;
    private LocalDate finishTime;
}
