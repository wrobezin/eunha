package io.github.wrobezin.eunha.crawler.data;

import io.github.wrobezin.eunha.crawler.anotation.DataOperatorFor;
import io.github.wrobezin.eunha.crawler.entity.CrawlResult;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.data.repository.elasticsearch.PageElasticsearchRepository;
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
 * @date 2020/4/4 16:45
 */
@Component
public class DataOpertorWraper {
    private static final String PACKAGE_PATH = "io.github.wrobezin.eunha.crawler.data";

    private Map<Class<?>, ContentDataOperator> contentDataOperatorMap;

    private final PageElasticsearchRepository pageRepository;

    private final BeanHelper beanHelper;

    public DataOpertorWraper(PageElasticsearchRepository pageRepository, BeanHelper beanHelper) {
        this.pageRepository = pageRepository;
        this.beanHelper = beanHelper;
        this.contentDataOperatorMap = new HashMap<>(4);
        PackageScanUtils.classScan(PACKAGE_PATH)
                .stream()
                .filter(c -> c.isAnnotationPresent(DataOperatorFor.class))
                .forEach(c -> Optional.ofNullable(AnnotationUtils.findAnnotation(c, DataOperatorFor.class))
                        .map(DataOperatorFor::entityType)
                        .ifPresent(entityType -> contentDataOperatorMap.put(entityType, (ContentDataOperator) beanHelper.getBean(c))));
    }

    public CrawlResult savePageData(ParseResult parseResult) {
        return Optional.ofNullable(contentDataOperatorMap.get(parseResult.getContentType()))
                .map(operator -> operator.savePageData(parseResult))
                .orElse(null);
    }
}
