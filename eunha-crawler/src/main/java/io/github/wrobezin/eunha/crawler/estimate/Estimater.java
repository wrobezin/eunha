package io.github.wrobezin.eunha.crawler.estimate;

import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.data.entity.rule.AbstractInterestRuleItem;
import io.github.wrobezin.eunha.data.entity.rule.InterestRule;
import io.github.wrobezin.eunha.data.entity.rule.InterestRuleItemGroup;
import io.github.wrobezin.eunha.data.entity.rule.SingleInterestRuleItem;
import io.github.wrobezin.eunha.data.enums.RuleItemLogicTypeEnum;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static io.github.wrobezin.eunha.data.enums.RuleItemLogicTypeEnum.AND;

/**
 * 兴趣规则匹配器接口
 *
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:09
 */
public interface Estimater {
    double TRUE = 1.0;
    double FALSE = 0.0;
    DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 单项匹配
     *
     * @param content  内容
     * @param ruleItem 规则项
     * @return 匹配度
     */
    double fit(Object content, SingleInterestRuleItem ruleItem);

    /**
     * 连续or运算
     *
     * @param compatibilities 各项匹配度
     * @return 组匹配度
     */
    static double serialOr(List<Double> compatibilities) {
        return compatibilities.stream().max(Double::compareTo).orElse(FALSE);
    }

    /**
     * 连续and运算
     *
     * @param compatibilities 各项匹配度
     * @return 组匹配度
     */
    static double serialAnd(List<Double> compatibilities) {
        return compatibilities.stream().mapToDouble(Double::doubleValue).average().orElse(FALSE);
    }

    /**
     * 匹配度评估
     *
     * @param content   内容
     * @param ruleItems 规则项
     * @return 匹配度
     */
    default double estimate(Object content, List<? extends AbstractInterestRuleItem> ruleItems) {
        Queue<Double> compatibilities = new LinkedList<>();
        Queue<RuleItemLogicTypeEnum> logicTypes = new LinkedList<>();
        // 计算组内各项的匹配度
        ruleItems.forEach(item -> {
            if (item instanceof InterestRuleItemGroup) {
                // 递归计算子组
                compatibilities.offer(estimate(content, ((InterestRuleItemGroup) item).getRuleItems()));
            } else {
                // 计算单项
                compatibilities.offer(fit(content, (SingleInterestRuleItem) item));
            }
            logicTypes.offer(item.getLogicType());
        });
        // 执行整组的逻辑运算
        List<Double> orGroup = new ArrayList<>(ruleItems.size());
        List<Double> andGroup = new ArrayList<>(ruleItems.size());
        RuleItemLogicTypeEnum currentLogic = logicTypes.poll();
        while (!compatibilities.isEmpty()) {
            Double compatibility = compatibilities.poll();
            RuleItemLogicTypeEnum logic = logicTypes.poll();
            if (AND.equals(logic)) {
                andGroup.add(compatibility);
            } else if (AND.equals(currentLogic)) {
                andGroup.add(compatibility);
                orGroup.add(serialAnd(andGroup));
                andGroup.clear();
            } else {
                orGroup.add(compatibility);
            }
            currentLogic = logic;
        }
        return serialOr(orGroup);
    }

    /**
     * 匹配度评估
     *
     * @param parseResult  解析结果
     * @param interestRule 兴趣规则
     * @return 匹配度
     */
    default double estimate(ParseResult parseResult, InterestRule interestRule) {
        return estimate(parseResult.getContent(), interestRule.getInterestRules());
    }
}
