package io.github.wrobezin.eunha.data.repository.mongo;

import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:55
 */
public interface CustomizedRuleMongoRepository extends MongoRepository<CustomizedRule, String> {
    /**
     * 根据规则名获取规则
     *
     * @param name 规则名
     * @return 规则
     */
    CustomizedRule findByName(String name);
}
