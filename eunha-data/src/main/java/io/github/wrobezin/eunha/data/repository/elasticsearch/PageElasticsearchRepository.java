package io.github.wrobezin.eunha.data.repository.elasticsearch;

import io.github.wrobezin.eunha.data.entity.document.Page;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:59
 */
public interface PageElasticsearchRepository extends ElasticsearchRepository<Page, String> {
    /**
     * 根据URL获取页面
     *
     * @param url URL
     * @return 页面列表
     */
    Page findByUrl(String url);
}
