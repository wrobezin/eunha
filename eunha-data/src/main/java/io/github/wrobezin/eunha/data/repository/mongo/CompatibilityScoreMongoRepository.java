package io.github.wrobezin.eunha.data.repository.mongo;

import io.github.wrobezin.eunha.data.entity.document.CompatibilityScore;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:55
 */
public interface CompatibilityScoreMongoRepository extends MongoRepository<CompatibilityScore, String> {
    /**
     * 通过URL和规则ID获取匹配度评分
     *
     * @param url
     * @param ruleId
     * @return
     */
    CompatibilityScore findByUrlAndRuleId(String url, String ruleId);
}
