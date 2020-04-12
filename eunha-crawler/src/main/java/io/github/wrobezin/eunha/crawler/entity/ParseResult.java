package io.github.wrobezin.eunha.crawler.entity;

import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 解析结果
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/30 14:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParseResult {
    private List<HyperLink> links;
    private Object content;
    private Class<?> contentType;
    private String title;
}
