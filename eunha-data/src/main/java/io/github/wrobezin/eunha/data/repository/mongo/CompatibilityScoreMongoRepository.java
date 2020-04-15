package io.github.wrobezin.eunha.data.repository.mongo;

import io.github.wrobezin.eunha.data.entity.document.CompatibilityScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:55
 */
public interface CompatibilityScoreMongoRepository extends MongoRepository<CompatibilityScore, String> {
    /**
     * 通过URL和规则ID获取匹配度评分
     *
     * @param url    URL
     * @param ruleId 规则ID
     * @return 匹配度评分
     */
    CompatibilityScore findByUrlAndRuleId(String url, String ruleId);

    /**
     * 获取大于或等于特定值的匹配度评分
     *
     * @param ruleId   规则ID
     * @param value    值
     * @param pageable 分页参数
     * @return 匹配度评分列表
     */
    List<CompatibilityScore> findByRuleIdAndValueGreaterThanEqual(String ruleId, Double value, Pageable pageable);
}
