package io.github.wrobezin.eunha.crawler.data;

import io.github.wrobezin.eunha.crawler.anotation.DataOperatorFor;
import io.github.wrobezin.eunha.crawler.entity.CrawlResult;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.data.entity.document.OriginalDocument;
import io.github.wrobezin.eunha.data.repository.elasticsearch.OriginalDocumentElasticsearchRepository;
import io.github.wrobezin.eunha.data.repository.mongo.OriginalDocumentMongoRepository;
import io.github.wrobezin.eunha.data.utils.EntityHashUtils;

import java.time.LocalDateTime;
import java.util.Optional;

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
    public CrawlResult savePageData(ParseResult parseResult) {
        CrawlResult crawlResult = new CrawlResult();
        OriginalDocument document = (OriginalDocument) parseResult.getContent();
        OriginalDocument documentInMongo = mongoRepository.findFirstByUrlOrderByVersionDesc(document.getUrl());
        String fingerPrint = EntityHashUtils.generateOriginalDocumentFingerPrint(document);
        String esId = EntityHashUtils.generateUrlHash(document.getUrl());
        int dbVersion = Optional.ofNullable(documentInMongo).map(OriginalDocument::getVersion).orElse(0);
        boolean newPage = documentInMongo == null;
        crawlResult.setNewPage(newPage);
        crawlResult.setUrl(document.getUrl());
        crawlResult.setContentType(OriginalDocument.class.getSimpleName());
        if (newPage || !fingerPrint.equals(documentInMongo.getFingerPrint())) {
            document.setFingerPrint(fingerPrint);
            int version = dbVersion + 1;
            document.setVersion(version);
            document.setUpdateTime(LocalDateTime.now());
            String mongoId = EntityHashUtils.generateContentMongoId(document.getFingerPrint(), document.getVersion(), document.getUpdateTime());
            document.setId(mongoId);
            mongoRepository.save(document);
            document.setId(esId);
            esRepository.save(document);
            crawlResult.setUpdated(true);
            crawlResult.setVersion(version);
            crawlResult.setContentDbId(mongoId);
            crawlResult.setFinishTime(LocalDateTime.now());
            return crawlResult;
        }
        crawlResult.setUpdated(false);
        crawlResult.setVersion(dbVersion);
        crawlResult.setContentDbId(documentInMongo.getId());
        crawlResult.setFinishTime(LocalDateTime.now());
        return crawlResult;
    }

    @Override
    public Object getContentFromEsById(String id) {
        return esRepository.findById(id).orElse(OriginalDocument.BLANK);
    }

    @Override
    public Object getContentFromEsByUrl(String url) {
        return esRepository.findByUrl(url);
    }
}
