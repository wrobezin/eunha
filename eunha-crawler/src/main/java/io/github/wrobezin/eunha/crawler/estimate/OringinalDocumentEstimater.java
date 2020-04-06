package io.github.wrobezin.eunha.crawler.estimate;

import io.github.wrobezin.eunha.crawler.anotation.EstimaterFor;
import io.github.wrobezin.eunha.data.entity.document.OriginalDocument;
import io.github.wrobezin.eunha.data.entity.rule.SingleInterestRuleItem;

import java.time.LocalDateTime;

/**
 * 原始文档兴趣规则匹配器
 *
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:11
 */
@EstimaterFor(OriginalDocument.class)
public class OringinalDocumentEstimater implements Estimater {
    @Override
    public double fit(Object content, SingleInterestRuleItem ruleItem) {
        if (!(content instanceof OriginalDocument)) {
            return FALSE;
        }
        OriginalDocument document = (OriginalDocument) content;
        switch (ruleItem.getJudgeType()) {
            case TITLE_CONTAIN:
                return document.getTitle().contains(ruleItem.getValue()) ? TRUE : FALSE;
            case TITLE_NOT_CONTAIN:
                return !document.getTitle().contains(ruleItem.getValue()) ? TRUE : FALSE;
            case CONTENT_CONTAIN:
                return document.getBody().contains(ruleItem.getValue()) ? TRUE : FALSE;
            case CONTENT_NOT_CONTAIN:
                return !document.getBody().contains(ruleItem.getValue()) ? TRUE : FALSE;
            case LATER_THAN:
                return document.getUpdateTime().isAfter(LocalDateTime.parse(ruleItem.getValue())) ? TRUE : FALSE;
            case EARLIER_THAN:
                return document.getUpdateTime().isBefore(LocalDateTime.parse(ruleItem.getValue())) ? TRUE : FALSE;
            case LONGER_THAN:
                return document.getBody().length() >= Integer.parseInt(ruleItem.getValue()) ? TRUE : FALSE;
            case SHORTER_THAN:
                return document.getBody().length() <= Integer.parseInt(ruleItem.getValue()) ? TRUE : FALSE;
        }
        return FALSE;
    }
}
