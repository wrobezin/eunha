package io.github.wrobezin.eunha.crawler.data;

import io.github.wrobezin.eunha.crawler.anotation.DataOperatorFor;
import io.github.wrobezin.eunha.crawler.entity.CrawlResult;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.data.entity.document.Page;
import io.github.wrobezin.eunha.data.repository.elasticsearch.PageElasticsearchRepository;
import io.github.wrobezin.eunha.data.utils.EntityHashUtils;
import io.github.wrobezin.framework.utils.http.HttpUrlUtils;
import io.github.wrobezin.framework.utils.spring.BeanHelper;
import io.github.wrobezin.framework.utils.spring.PackageScanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    private Map<String, ContentDataOperator> contentDataOperatorMap;

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
                        .ifPresent(entityType -> contentDataOperatorMap.put(entityType.getSimpleName(), (ContentDataOperator) beanHelper.getBean(c))));
    }

    public CrawlResult savePageData(ParseResult parseResult) {
        CrawlResult crawlResult = Optional.ofNullable(contentDataOperatorMap.get(parseResult.getContentType().getSimpleName()))
                .map(operator -> operator.savePageData(parseResult))
                .orElse(CrawlResult.NO_RESULT);
        String url = crawlResult.getUrl();
        String pageId = EntityHashUtils.generateUrlHash(url);
        crawlResult.setEsId(pageId);
        if (crawlResult.getUpdated()) {
            Page page = pageRepository.findById(pageId)
                    .orElse(Page.builder()
                            .id(pageId)
                            .host(HttpUrlUtils.parseUrl(url).getHost())
                            .url(url)
                            .build());
            page.setHyperLinks(parseResult.getLinks());
            page.setContentType(crawlResult.getContentType());
            page.setUpdateTime(LocalDateTime.now());
            page.setTitle(parseResult.getTitle());
            pageRepository.save(page);
        }
        return crawlResult;
    }

    public Object getContent(String contentType, String id) {
        return Optional.ofNullable(contentDataOperatorMap.get(contentType))
                .map(operator -> operator.getContentFromEsById(id))
                .orElse(null);
    }
}
