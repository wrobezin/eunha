package io.github.wrobezin.eunha.data.repository.mongo;

import io.github.wrobezin.eunha.data.entity.document.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/14 15:29
 */
public interface PageMongoRepository extends MongoRepository<Page, String> {

    /**
     * 根据URL获取最新版页面
     *
     * @param url URL
     * @return 最新版页面
     */
    Page findFirstByUrlOrderByVersionDesc(String url);
}
