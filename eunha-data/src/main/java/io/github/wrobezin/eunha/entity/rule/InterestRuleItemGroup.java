package io.github.wrobezin.eunha.entity.rule;

import io.github.wrobezin.eunha.enums.RuleItemLogicTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class InterestRuleItemGroup extends AbstractInterestRuleItem {
    private List<? extends AbstractInterestRuleItem> ruleItems;

    public InterestRuleItemGroup(List<? extends AbstractInterestRuleItem> ruleItems) {
        if (ruleItems.size() == 0) {
            throw new UnsupportedOperationException("规则项组为空");
        } else if (!ruleItems.get(0).logicType.equals(RuleItemLogicTypeEnum.FIRST)) {
            throw new UnsupportedOperationException("规则项组第一项的逻辑类型必须为FIRST");
        }
        this.ruleItems = ruleItems;
    }

    @Override
    public String toString() {
        return "(" + this.logicType.getDescription() + ":" + ruleItems.stream().map(Objects::toString).collect(Collectors.joining(",")) + ")";
    }
}
