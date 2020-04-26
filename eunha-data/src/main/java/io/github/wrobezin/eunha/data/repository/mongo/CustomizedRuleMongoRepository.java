package io.github.wrobezin.eunha.data.repository.mongo;

import io.github.wrobezin.eunha.data.entity.rule.CustomizedRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * @param name     规则名
     * @param pageable 分页参数
     * @return 规则列表
     */
    Page<CustomizedRule> findByNameLike(String name, Pageable pageable);

    /**
     * 获取匹配规则名的规则总数
     *
     * @param name 规则名
     * @return 规则总数
     */
    Integer countByNameLike(String name);
}
