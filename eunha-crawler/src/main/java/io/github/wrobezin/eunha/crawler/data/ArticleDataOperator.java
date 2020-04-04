package io.github.wrobezin.eunha.crawler.data;

import io.github.wrobezin.eunha.crawler.anotation.DataOperatorFor;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.data.entity.document.Article;
import io.github.wrobezin.eunha.data.repository.elasticsearch.ArticleElasticsearchRepository;
import io.github.wrobezin.eunha.data.repository.mongo.ArticleMongoRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 19:58
 */
@DataOperatorFor(Article.class)
public class ArticleDataOperator implements ContentDataOperator {
    private final ArticleMongoRepository mongoRepository;

    private final ArticleElasticsearchRepository esRepository;

    public ArticleDataOperator(ArticleMongoRepository mongoRepository, ArticleElasticsearchRepository esRepository) {
        this.mongoRepository = mongoRepository;
        this.esRepository = esRepository;
    }

    @Override
    public String saveMongo(Object content) {
        return null;
    }

    @Override
    public String saveElasticsearch(Object content) {
        return null;
    }
}
