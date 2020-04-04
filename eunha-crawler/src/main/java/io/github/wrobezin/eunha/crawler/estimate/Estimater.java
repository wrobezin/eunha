package io.github.wrobezin.eunha.crawler.estimate;

import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.entity.rule.InterestRule;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:09
 */
public interface Estimater {
    double estimate(ParseResult parseResult, InterestRule interestRule);
}
