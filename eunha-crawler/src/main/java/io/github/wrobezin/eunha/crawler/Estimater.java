package io.github.wrobezin.eunha.crawler;

import io.github.wrobezin.eunha.crawler.entity.HyperLinkToDownload;
import io.github.wrobezin.eunha.data.entity.document.CompatibilityScore;
import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.eunha.data.entity.document.Page;
import io.github.wrobezin.eunha.data.entity.rule.*;
import io.github.wrobezin.eunha.data.enums.RuleItemLogicTypeEnum;
import io.github.wrobezin.eunha.data.repository.mongo.CompatibilityScoreMongoRepository;
import io.github.wrobezin.framework.utils.http.HttpUrlUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.github.wrobezin.eunha.data.enums.RuleItemLogicTypeEnum.AND;

/**
 * 兴趣规则匹配器接口
 *
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:09
 */
@Component
public class Estimater {
    public static final double TRUE = 1.0;
    public static final double FALSE = 0.0;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static double HISTORY_SCORE_RATE = 0.4;
    private static double PARENT_PAGE_RATE = 0.2;
    private static double LINK_SELF_RATE = 0.4;

    private final CompatibilityScoreMongoRepository compatibilityRepository;

    public Estimater(CompatibilityScoreMongoRepository compatibilityRepository) {
        this.compatibilityRepository = compatibilityRepository;
    }

    /**
     * 连续or运算
     *
     * @param compatibilities 各项匹配度
     * @return 组匹配度
     */
    private static double serialOr(List<Double> compatibilities) {
        return compatibilities.stream().max(Double::compareTo).orElse(FALSE);
    }

    /**
     * 连续and运算
     *
     * @param compatibilities 各项匹配度
     * @return 组匹配度
     */
    private static double serialAnd(List<Double> compatibilities) {
        return compatibilities.stream().mapToDouble(Double::doubleValue).average().orElse(FALSE);
    }

    private double pageFit(Page page, SingleInterestRuleItem ruleItem) {
        switch (ruleItem.getJudgeType()) {
            case ALWAYS_TRUE:
                return TRUE;
            case TITLE_CONTAIN:
                return page.getTitle().contains(ruleItem.getValue()) ? TRUE : FALSE;
            case TITLE_NOT_CONTAIN:
                return !page.getTitle().contains(ruleItem.getValue()) ? TRUE : FALSE;
            case CONTENT_CONTAIN:
                return page.getBody().contains(ruleItem.getValue()) ? TRUE : FALSE;
            case CONTENT_NOT_CONTAIN:
                return !page.getBody().contains(ruleItem.getValue()) ? TRUE : FALSE;
            case LATER_THAN:
                return page.getUpdateTime().isAfter(LocalDateTime.parse(ruleItem.getValue())) ? TRUE : FALSE;
            case EARLIER_THAN:
                return page.getUpdateTime().isBefore(LocalDateTime.parse(ruleItem.getValue())) ? TRUE : FALSE;
            case LONGER_THAN:
                return page.getBody().length() >= Integer.parseInt(ruleItem.getValue()) ? TRUE : FALSE;
            case SHORTER_THAN:
                return page.getBody().length() <= Integer.parseInt(ruleItem.getValue()) ? TRUE : FALSE;
        }
        return FALSE;
    }

    private double linkFit(HyperLink link, SingleInterestRuleItem ruleItem) {
        switch (ruleItem.getJudgeType()) {
            case TITLE_CONTAIN:
            case CONTENT_CONTAIN:
                return link.getAnchorText().contains(ruleItem.getValue()) ? TRUE : FALSE;
            case TITLE_NOT_CONTAIN:
            case CONTENT_NOT_CONTAIN:
                return !link.getAnchorText().contains(ruleItem.getValue()) ? TRUE : FALSE;
        }
        return FALSE;
    }

    /**
     * 匹配度评估
     *
     * @param obj       待评估对象
     * @param ruleItems 规则项
     * @return 匹配度
     */
    private double estimate(Object obj, List<? extends AbstractInterestRuleItem> ruleItems) {
        Queue<Double> compatibilities = new LinkedList<>();
        Queue<RuleItemLogicTypeEnum> logicTypes = new LinkedList<>();
        // 计算组内各项的匹配度
        ruleItems.forEach(item -> {
            if (item instanceof InterestRuleItemGroup) {
                // 递归计算子组
                compatibilities.offer(estimate(obj, ((InterestRuleItemGroup) item).getRuleItems()));
            } else {
                double value = obj instanceof Page ? pageFit((Page) obj, (SingleInterestRuleItem) item)
                        : linkFit((HyperLink) obj, (SingleInterestRuleItem) item);
                compatibilities.offer(value);
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

    public double estimate(Page page, InterestRule interestRule) {
        return estimate(page, interestRule.getInterestRules());
    }

    private double estimate(HyperLink link, InterestRule interestRule) {
        return estimate(link, interestRule.getInterestRules());
    }

    public double estimate(Double pageCompatibility, HyperLinkToDownload link, CustomizedRule rule) {
        String url = HttpUrlUtils.parseUrl(link.getLink().getUrl()).getUrlWithQuery();
        double history = getHistoryScore(url, rule.getId());
        return history * HISTORY_SCORE_RATE
                + pageCompatibility * PARENT_PAGE_RATE
                + estimate(link.getLink(), rule.getInterestRule()) * LINK_SELF_RATE;
    }

    public double getHistoryScore(String url, String ruleId) {
        return Optional.ofNullable(compatibilityRepository.findByUrlAndRuleId(url, ruleId))
                .map(CompatibilityScore::getValue)
                .orElse(FALSE);
    }
}
