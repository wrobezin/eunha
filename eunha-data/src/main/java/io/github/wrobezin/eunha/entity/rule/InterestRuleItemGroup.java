package io.github.wrobezin.eunha.entity.rule;

import io.github.wrobezin.eunha.enums.RuleItemLogicTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 兴趣规则项组
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 20:28
 */
@Data
@EqualsAndHashCode(callSuper = true)
class InterestRuleItemGroup extends AbstractInterestRuleItem {
    private List<AbstractInterestRuleItem> ruleItems;

    public InterestRuleItemGroup(AbstractInterestRuleItem... ruleItems) {
        if (ruleItems.length == 0) {
            throw new UnsupportedOperationException("规则项组为空");
        } else if (!ruleItems[0].logicType.equals(RuleItemLogicTypeEnum.FIRST)) {
            throw new UnsupportedOperationException("规则项组第一项的逻辑类型必须为FIRST");
        }
        this.ruleItems = Arrays.asList(ruleItems);
    }

    @Override
    public String toString() {
        return "(" + this.logicType.getDescription() + ":" + ruleItems.stream().map(Objects::toString).collect(Collectors.joining(",")) + ")";
    }
}
