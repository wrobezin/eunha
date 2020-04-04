package io.github.wrobezin.eunha.data.repository.elasticsearch;

import io.github.wrobezin.eunha.data.entity.document.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:59
 */
public interface ArticleElasticsearchRepository extends ElasticsearchRepository<Article, String> {
}
