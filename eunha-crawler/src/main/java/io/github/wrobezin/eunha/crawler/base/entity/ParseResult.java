package io.github.wrobezin.eunha.crawler.base.entity;

import io.github.wrobezin.eunha.entity.document.HyperLink;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 解析结果
 *
 * @param <T> 内容类型
 * @author yuan
 * @version 1.0
 * @date 2020/3/30 14:56
 */
@Data
@Builder
@NoArgsConstructor
public class ParseResult<T> {
    String url;
    List<HyperLink> links;
    T content;
}
