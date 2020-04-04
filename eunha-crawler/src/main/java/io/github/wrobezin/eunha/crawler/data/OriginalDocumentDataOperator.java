package io.github.wrobezin.eunha.crawler.data;

import io.github.wrobezin.eunha.data.repository.mongo.OriginalDocumentMongoRepository;
import io.github.wrobezin.eunha.data.entity.document.OriginalDocument;
import io.github.wrobezin.eunha.data.repository.elasticsearch.OriginalDocumentElasticsearchRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 17:05
 */
@DataOperatorFor(OriginalDocument.class)
public class OriginalDocumentDataOperator {
    private final OriginalDocumentMongoRepository mongoRepository;

    private final OriginalDocumentElasticsearchRepository esRepository;

    public OriginalDocumentDataOperator(OriginalDocumentMongoRepository mongoRepository, OriginalDocumentElasticsearchRepository esRepository) {
        this.mongoRepository = mongoRepository;
        this.esRepository = esRepository;
    }
}
