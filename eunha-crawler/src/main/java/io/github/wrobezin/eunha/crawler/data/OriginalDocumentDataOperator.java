package io.github.wrobezin.eunha.crawler.data;

import io.github.wrobezin.eunha.crawler.anotation.DataOperatorFor;
import io.github.wrobezin.eunha.data.entity.document.OriginalDocument;
import io.github.wrobezin.eunha.data.repository.elasticsearch.OriginalDocumentElasticsearchRepository;
import io.github.wrobezin.eunha.data.repository.mongo.OriginalDocumentMongoRepository;
import io.github.wrobezin.eunha.data.utils.EntityHashUtils;

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

    public OriginalDocument getNewest(String url){
        return mongoRepository.findFirstByUrlOrderByVersionDesc(url);
    }

    @Override
    public String saveMongo(Object content) {
        OriginalDocument document = (OriginalDocument) content;
        String fingerPrint = EntityHashUtils.generateOriginalFingerPrint(document);
        return null;
    }

    @Override
    public String saveElasticsearch(Object content) {
        return null;
    }
}
