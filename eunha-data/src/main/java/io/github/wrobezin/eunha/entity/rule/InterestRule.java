package io.github.wrobezin.eunha.entity.rule;

import io.github.wrobezin.eunha.enums.RuleItemJudgeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static io.github.wrobezin.eunha.enums.RuleItemJudgeTypeEnum.TITLE_CONTAIN;
import static io.github.wrobezin.eunha.enums.RuleItemLogicTypeEnum.*;

/**
 * 兴趣规则
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 20:05
 */
@Data
@AllArgsConstructor
public class InterestRule {
    private List<InterestRuleItemGroup> interestRules;

    public static InterestRuleItemGroup group(List<? extends AbstractInterestRuleItem> ruleItems) {
        return new InterestRuleItemGroup(ruleItems);
    }

    public static InterestRule first(InterestRuleItemGroup group) {
        InterestRule interestRule = new InterestRule(new LinkedList<>());
        group.setLogicType(FIRST);
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
        group.setLogicType(OR);
        this.interestRules.add(group);
        return this;
    }

    public InterestRule or(RuleItemJudgeTypeEnum judgeType, String value) {
        return or(new InterestRuleItemGroup(Collections.singletonList(SingleInterestRuleItem.first(judgeType, value))));
    }

    public InterestRule andNot(InterestRuleItemGroup group) {
        group.setLogicType(AND_NOT);
        this.interestRules.add(group);
        return this;
    }

    public InterestRule andNot(RuleItemJudgeTypeEnum judgeType, String value) {
        return andNot(new InterestRuleItemGroup(Collections.singletonList(SingleInterestRuleItem.first(judgeType, value))));
    }

    public InterestRule orNot(InterestRuleItemGroup group) {
        group.setLogicType(OR_NOT);
        this.interestRules.add(group);
        return this;
    }

    public InterestRule orNot(RuleItemJudgeTypeEnum judgeType, String value) {
        return orNot(new InterestRuleItemGroup(Collections.singletonList(SingleInterestRuleItem.first(judgeType, value))));
    }

    public static void main(String[] args) {
        InterestRule rule1 = InterestRule.first(TITLE_CONTAIN, "武汉").and(TITLE_CONTAIN, "新冠肺炎");
        System.out.println(rule1);
        System.out.println("---------------------------------------");

        InterestRule rule2 = InterestRule.first(TITLE_CONTAIN, "武汉")
                .and(group(Arrays.asList(
                        SingleInterestRuleItem.first(TITLE_CONTAIN, "新冠肺炎"),
                        SingleInterestRuleItem.or(TITLE_CONTAIN, "新冠病毒"),
                        SingleInterestRuleItem.or(TITLE_CONTAIN, "新冠肺炎病毒"),
                        SingleInterestRuleItem.or(TITLE_CONTAIN, "新型冠状肺炎"),
                        SingleInterestRuleItem.or(TITLE_CONTAIN, "新型冠状肺炎病毒"),
                        SingleInterestRuleItem.or(TITLE_CONTAIN, "新型冠状病毒"))));
        System.out.println(rule2);
        System.out.println("---------------------------------------");
    }
}
