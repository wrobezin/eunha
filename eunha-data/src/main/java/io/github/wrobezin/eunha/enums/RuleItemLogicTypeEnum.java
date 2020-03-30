package io.github.wrobezin.eunha.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则项逻辑类型枚举
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 20:17
 */
@Getter
@AllArgsConstructor
public enum RuleItemLogicTypeEnum {
    /** 第一个规则项，前面不带逻辑运算符 **/
    FIRST(0, "首项"),
    AND(1, "与"),
    OR(2, "或"),
    AND_NOT(3, "与非"),
    OR_NOT(4, "或非");

    private Integer logic;
    private String description;
}
