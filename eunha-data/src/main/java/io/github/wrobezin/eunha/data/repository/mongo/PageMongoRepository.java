package io.github.wrobezin.eunha.data.repository.mongo;

import io.github.wrobezin.eunha.data.entity.document.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:56
 */
public interface PageMongoRepository extends MongoRepository<Page, String> {
}
