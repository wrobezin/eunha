package io.github.wrobezin.eunha.crawler.data;

import io.github.wrobezin.eunha.data.entity.document.Article;
import io.github.wrobezin.eunha.data.repository.elasticsearch.ArticleElasticsearchRepository;
import io.github.wrobezin.eunha.data.repository.mongo.ArticleMongoRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:48
 */
@DataOperatorFor(Article.class)
public class ArticleDataOperator {
    private final ArticleMongoRepository mongoRepository;

    private final ArticleElasticsearchRepository esRepository;

    public ArticleDataOperator(ArticleMongoRepository mongoRepository, ArticleElasticsearchRepository esRepository) {
        this.mongoRepository = mongoRepository;
        this.esRepository = esRepository;
    }
}
