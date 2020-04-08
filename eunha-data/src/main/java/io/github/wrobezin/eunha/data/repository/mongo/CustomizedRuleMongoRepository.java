package io.github.wrobezin.eunha.data.repository.mongo;

import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:55
 */
public interface CustomizedRuleMongoRepository extends MongoRepository<CustomizedRule, String> {
}
