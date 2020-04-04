package io.github.wrobezin.eunha.crawler.data;

import io.github.wrobezin.eunha.data.repository.elasticsearch.PageElasticsearchRepository;
import io.github.wrobezin.eunha.data.entity.document.Page;
import io.github.wrobezin.eunha.data.repository.mongo.PageMongoRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 17:03
 */
@DataOperatorFor(Page.class)
public class PageDataOperator {
    private final PageMongoRepository mongoRepository;

    private final PageElasticsearchRepository esRepository;

    public PageDataOperator(PageMongoRepository mongoRepository, PageElasticsearchRepository esRepository) {
        this.mongoRepository = mongoRepository;
        this.esRepository = esRepository;
    }
}
