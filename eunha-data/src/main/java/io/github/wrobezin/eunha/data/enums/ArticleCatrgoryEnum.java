package io.github.wrobezin.eunha.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文章分类枚举
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 19:48
 */
@Getter
@AllArgsConstructor
public enum ArticleCatrgoryEnum {
    /**
     * 无内容或解析错误时使用
     */
    BLANK(-1, "空"),
    /**
     * 未知类别，有内容；默认分类
     */
    UNKNOWN(0, "未知");

    private Integer code;
    private String name;
}
