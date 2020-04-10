package io.github.wrobezin.eunha.crawler.estimate;

import io.github.wrobezin.eunha.crawler.anotation.EstimaterFor;
import io.github.wrobezin.eunha.crawler.entity.HyperLinkToDownload;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.data.entity.document.CompatibilityScore;
import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import io.github.wrobezin.eunha.data.entity.rule.InterestRule;
import io.github.wrobezin.eunha.data.repository.mongo.CompatibilityScoreMongoRepository;
import io.github.wrobezin.framework.utils.http.HttpUrlUtils;
import io.github.wrobezin.framework.utils.spring.BeanHelper;
import io.github.wrobezin.framework.utils.spring.PackageScanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/6 19:59
 */
@Component
public class EstimaterRouter {
    private static final String PACKAGE_PATH = "io.github.wrobezin.eunha.crawler.estimate";
    private Map<Class<?>, Estimater> estimaterMap;

    private static double HISTORY_SCORE_RATE = 0.4;
    private static double PARENT_PAGE_RATE = 0.3;
    private static double LINK_SELF_RATE = 0.3;

    private final BeanHelper beanHelper;

    private final CompatibilityScoreMongoRepository compatibilityRepository;

    public EstimaterRouter(BeanHelper beanHelper, CompatibilityScoreMongoRepository compatibilityRepository) {
        this.compatibilityRepository = compatibilityRepository;
        this.estimaterMap = new HashMap<>(4);
        this.beanHelper = beanHelper;
        PackageScanUtils.classScan(PACKAGE_PATH)
                .stream()
                .filter(c -> c.isAnnotationPresent(EstimaterFor.class))
                .forEach(c -> Optional.ofNullable(AnnotationUtils.findAnnotation(c, EstimaterFor.class))
                        .map(EstimaterFor::entityType)
                        .ifPresent(entityType -> estimaterMap.put(entityType, (Estimater) beanHelper.getBean(c))));
    }

    public double estimate(ParseResult parseResult, InterestRule interestRule) {
        return estimaterMap.get(parseResult.getContentType()).estimate(parseResult, interestRule);
    }

    private double estimate(HyperLinkToDownload link, InterestRule interestRule) {
        return estimaterMap.get(HyperLink.class).estimate(link.getLink(), interestRule.getInterestRules());
    }

    public double estimate(Double pageCompatibility, HyperLinkToDownload link, CustomizedRule rule) {
        String url = HttpUrlUtils.parseUrl(link.getLink().getUrl()).getUrlWithQuery();
        double history = Optional.ofNullable(compatibilityRepository.findByUrlAndRuleId(url, rule.getId()))
                .map(CompatibilityScore::getValue)
                .orElse(0.0);
        return history * HISTORY_SCORE_RATE
                + pageCompatibility * PARENT_PAGE_RATE
                + estimate(link, rule.getInterestRule()) * LINK_SELF_RATE;
    }
}
