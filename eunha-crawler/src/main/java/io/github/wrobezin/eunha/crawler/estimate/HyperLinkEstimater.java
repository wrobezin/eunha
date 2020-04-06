package io.github.wrobezin.eunha.crawler.estimate;

import io.github.wrobezin.eunha.crawler.anotation.EstimaterFor;
import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.eunha.data.entity.rule.SingleInterestRuleItem;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/6 20:44
 */
@EstimaterFor(HyperLink.class)
public class HyperLinkEstimater implements Estimater {
    @Override
    public double fit(Object content, SingleInterestRuleItem ruleItem) {
        HyperLink link = (HyperLink) content;
        switch (ruleItem.getJudgeType()) {
            case TITLE_CONTAIN:
            case CONTENT_CONTAIN:
                return link.getAnchorText().contains(ruleItem.getValue()) ? TRUE : FALSE;
            case TITLE_NOT_CONTAIN:
            case CONTENT_NOT_CONTAIN:
                return !link.getAnchorText().contains(ruleItem.getValue()) ? TRUE : FALSE;
        }
        return 0;
    }
}
