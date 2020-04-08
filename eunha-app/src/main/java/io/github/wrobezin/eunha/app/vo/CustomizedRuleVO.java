package io.github.wrobezin.eunha.app.vo;

import io.github.wrobezin.eunha.data.entity.rule.CrawlRule;
import io.github.wrobezin.eunha.data.enums.RuleItemJudgeTypeEnum;
import io.github.wrobezin.eunha.data.enums.RuleItemLogicTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/8 19:36
 */
@Data
public class CustomizedRuleVO {
    private String id;
    private String name;
    private CrawlRule crawlRule;
    private List<InterestItem> interestRule;

    @Data
    public static class InterestItem {
        private RuleItemJudgeTypeEnum type;
        private RuleItemLogicTypeEnum logic;
        private String value;
        private List<InterestItem> sub;
    }
}
