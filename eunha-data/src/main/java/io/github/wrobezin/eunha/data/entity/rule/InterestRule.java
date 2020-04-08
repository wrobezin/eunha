package io.github.wrobezin.eunha.data.entity.rule;

import io.github.wrobezin.eunha.data.enums.RuleItemJudgeTypeEnum;
import io.github.wrobezin.eunha.data.enums.RuleItemLogicTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static io.github.wrobezin.eunha.data.enums.RuleItemLogicTypeEnum.AND;
import static io.github.wrobezin.eunha.data.enums.RuleItemLogicTypeEnum.OR;

/**
 * 兴趣规则
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 20:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestRule {
    private List<AbstractInterestRuleItem> interestRules;

    public static InterestRuleItemGroup group(List<? extends AbstractInterestRuleItem> ruleItems) {
        return new InterestRuleItemGroup(ruleItems);
    }

    public static InterestRuleItemGroup andGroup(List<? extends AbstractInterestRuleItem> ruleItems) {
        return new InterestRuleItemGroup(AND,ruleItems);
    }

    public static InterestRuleItemGroup orGroup(List<? extends AbstractInterestRuleItem> ruleItems) {
        return new InterestRuleItemGroup(OR,ruleItems);
    }

    public static InterestRule first(InterestRuleItemGroup group) {
        InterestRule interestRule = new InterestRule(new LinkedList<>());
        group.setLogicType(RuleItemLogicTypeEnum.FIRST);
        interestRule.interestRules.add(group);
        return interestRule;
    }

    public static InterestRule first(RuleItemJudgeTypeEnum judgeType, String value) {
        return first(new InterestRuleItemGroup(Collections.singletonList(SingleInterestRuleItem.first(judgeType, value))));
    }

    public InterestRule and(InterestRuleItemGroup group) {
        group.setLogicType(AND);
        this.interestRules.add(group);
        return this;
    }

    public InterestRule and(RuleItemJudgeTypeEnum judgeType, String value) {
        return and(new InterestRuleItemGroup(Collections.singletonList(SingleInterestRuleItem.first(judgeType, value))));
    }

    public InterestRule or(InterestRuleItemGroup group) {
        group.setLogicType(RuleItemLogicTypeEnum.OR);
        this.interestRules.add(group);
        return this;
    }

    public InterestRule or(RuleItemJudgeTypeEnum judgeType, String value) {
        return or(new InterestRuleItemGroup(Collections.singletonList(SingleInterestRuleItem.first(judgeType, value))));
    }

    public static void main(String[] args) {
        InterestRule rule1 = InterestRule.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "武汉").and(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "新冠肺炎");
        System.out.println(rule1);
        System.out.println("---------------------------------------");

        InterestRule rule2 = InterestRule.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "武汉")
                .and(group(Arrays.asList(
                        SingleInterestRuleItem.first(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "新冠肺炎"),
                        SingleInterestRuleItem.or(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "新冠病毒"),
                        SingleInterestRuleItem.or(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "新冠肺炎病毒"),
                        SingleInterestRuleItem.or(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "新型冠状肺炎"),
                        SingleInterestRuleItem.or(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "新型冠状肺炎病毒"),
                        SingleInterestRuleItem.or(RuleItemJudgeTypeEnum.TITLE_CONTAIN, "新型冠状病毒"))));
        System.out.println(rule2);
        System.out.println("---------------------------------------");
    }
}
