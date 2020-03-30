package io.github.wrobezin.eunha.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 页面内容类型枚举
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/30 15:16
 */
@Getter
@AllArgsConstructor
public enum PageContentTypeEnum {
    /** 直接提取body标签内容 */
    ORIGINAL_HTML(0, "原始HTML"),
    /** 解析后的简单文本文章 */
    SIMPLE_TEXT_ARTICLE(1, "文章"),
    /** 解析后的富文本文章 */
    RICH_TEXT_ARTICLE(1, "富文本");

    private Integer type;
    private String description;
}
