package io.github.wrobezin.eunha.data.enums;

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
    /** 默认 */
    ALWAYS_TRUE(0, "永真"),
    /** 网页标题包含某个词组 */
    TITLE_CONTAIN(1, "标题包含"),
    /** 网页标题不含某个词组 */
    TITLE_NOT_CONTAIN(2, "标题不含"),
    /** 网页内容包含某个词组 */
    CONTENT_CONTAIN(3, "内容包含"),
    /** 网页内容不含某个词组 */
    CONTENT_NOT_CONTAIN(4, "内容不含"),
    /** 抓取时间早于或等于某个值 */
    EARLIER_THAN(5, "早于"),
    /** 抓取时间晚于或等于某个值 */
    LATER_THAN(6, "晚于"),
    /** 页面长度大于或等于某个值 */
    LONGER_THAN(7, "长于"),
    /** 页面长度小于或等于某个值 */
    SHORTER_THAN(8, "短于");

    private Integer type;
    private String description;
}
