package io.github.wrobezin.eunha.crawler.data;

import io.github.wrobezin.eunha.crawler.anotation.DataOperatorFor;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.data.entity.document.OriginalDocument;
import io.github.wrobezin.eunha.data.repository.elasticsearch.OriginalDocumentElasticsearchRepository;
import io.github.wrobezin.eunha.data.repository.mongo.OriginalDocumentMongoRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 20:24
 */
@DataOperatorFor(OriginalDocument.class)
public class OriginalDocumentDataOperator implements ContentDataOperator {
    private final OriginalDocumentMongoRepository mongoRepository;

    private final OriginalDocumentElasticsearchRepository esRepository;

    public OriginalDocumentDataOperator(OriginalDocumentMongoRepository mongoRepository, OriginalDocumentElasticsearchRepository esRepository) {
        this.mongoRepository = mongoRepository;
        this.esRepository = esRepository;
    }

    @Override
    public String saveMongo(ParseResult parseResult) {
        return null;
    }

    @Override
    public String saveElasticsearch(ParseResult parseResult) {
        return null;
    }
}
