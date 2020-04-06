package io.github.wrobezin.eunha.data.repository.mongo;

import io.github.wrobezin.eunha.data.entity.document.OriginalDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:57
 */
public interface OriginalDocumentMongoRepository extends MongoRepository<OriginalDocument, String> {
    /**
     * 根据URL获取版本号最大的原始文档
     *
     * @param url url
     * @return 对应URL的最新版原始文档
     */
    OriginalDocument findFirstByUrlOrderByVersionDesc(String url);

    /**
     * 根据URL删除全部文档
     *
     * @param url url
     */
    void deleteAllByUrl(String url);
}
