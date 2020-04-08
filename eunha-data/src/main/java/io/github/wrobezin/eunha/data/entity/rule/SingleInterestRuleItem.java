package io.github.wrobezin.eunha.data.entity.rule;

import io.github.wrobezin.eunha.data.enums.RuleItemJudgeTypeEnum;
import io.github.wrobezin.eunha.data.enums.RuleItemLogicTypeEnum;
import lombok.*;

/**
 * 单一兴趣规则项
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 20:44
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SingleInterestRuleItem extends AbstractInterestRuleItem {
    private RuleItemJudgeTypeEnum judgeType;
    // TODO 换成List或Map以支持更复杂的兴趣规则
    private String value;

    private SingleInterestRuleItem(RuleItemLogicTypeEnum logicType, RuleItemJudgeTypeEnum judgeType, String value) {
        super.logicType = logicType;
        this.judgeType = judgeType;
        this.value = value;
    }

    public static SingleInterestRuleItem first(RuleItemJudgeTypeEnum judgeType, String value) {
        return new SingleInterestRuleItem(RuleItemLogicTypeEnum.FIRST, judgeType, value);
    }

    public static SingleInterestRuleItem and(RuleItemJudgeTypeEnum judgeType, String value) {
        return new SingleInterestRuleItem(RuleItemLogicTypeEnum.AND, judgeType, value);
    }

    public static SingleInterestRuleItem or(RuleItemJudgeTypeEnum judgeType, String value) {
        return new SingleInterestRuleItem(RuleItemLogicTypeEnum.OR, judgeType, value);
    }

    @Override
    public String toString() {
        return "<" + logicType.getDescription() + ";" + judgeType.getDescription() + ";" + value + ">";
    }
}
