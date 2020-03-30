package io.github.wrobezin.eunha.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则项判断类型枚举
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 20:36
 */
@Getter
@AllArgsConstructor
public enum RuleItemJudgeTypeEnum {
    /** 网页标题包含某个词组 */
    TITLE_CONTAIN(2, "标题包含"),
    /** 网页标题不含某个词组 */
    TITLE_NOT_CONTAIN(3, "标题不含"),
    /** 网页内容包含某个词组 */
    CONTENT_CONTAIN(2, "内容包含"),
    /** 网页内容不含某个词组 */
    CONTENT_NOT_CONTAIN(3, "内容不含"),
    /** 抓取时间早于或等于某个值 */
    EARLIER_THAN(4, "早于"),
    /** 抓取时间晚于或等于某个值 */
    LATER_THAN(5, "晚于"),
    /** 页面长度大于或等于某个值 */
    LONGER_THAN(6, "长于"),
    /** 页面长度小于或等于某个值 */
    SHORTER_THAN(7, "短于");

    private Integer type;
    private String description;
}
