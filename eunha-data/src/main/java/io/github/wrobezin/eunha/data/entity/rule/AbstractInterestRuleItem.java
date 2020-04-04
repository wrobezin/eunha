package io.github.wrobezin.eunha.data.entity.rule;

import io.github.wrobezin.eunha.data.enums.RuleItemLogicTypeEnum;
import lombok.Data;

/**
 * 抽象兴趣规则项
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 20:30
 */
@Data
abstract class AbstractInterestRuleItem {
    protected RuleItemLogicTypeEnum logicType;
}
