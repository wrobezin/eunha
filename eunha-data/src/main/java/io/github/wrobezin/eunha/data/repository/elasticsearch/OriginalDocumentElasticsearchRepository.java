package io.github.wrobezin.eunha.data.repository.elasticsearch;

import io.github.wrobezin.eunha.data.entity.document.OriginalDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:59
 */
public interface OriginalDocumentElasticsearchRepository extends ElasticsearchRepository<OriginalDocument, String> {
    /**
     * 通过URL获取原始文档
     *
     * @param url url
     * @return 原始文档
     */
    OriginalDocument findByUrl(String url);
}
