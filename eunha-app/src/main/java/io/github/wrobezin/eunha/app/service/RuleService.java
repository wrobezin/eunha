package io.github.wrobezin.eunha.app.service;

import io.github.wrobezin.eunha.app.vo.CustomizedRuleVO;
import io.github.wrobezin.eunha.data.entity.rule.*;
import io.github.wrobezin.eunha.data.repository.mongo.CustomizedRuleMongoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/8 20:42
 */
@Service
public class RuleService {
    private final CustomizedRuleMongoRepository repository;

    public RuleService(CustomizedRuleMongoRepository repository) {
        this.repository = repository;
    }

    private AbstractInterestRuleItem parseInterestItem(CustomizedRuleVO.InterestItem item) {
        if (null == item.getSub()) {
            SingleInterestRuleItem ruleItem = new SingleInterestRuleItem();
            ruleItem.setLogicType(item.getLogic());
            ruleItem.setJudgeType(item.getType());
            ruleItem.setValue(item.getValue());
            return ruleItem;
        }
        InterestRuleItemGroup group = new InterestRuleItemGroup(item.getSub().stream()
                .map(this::parseInterestItem)
                .collect(Collectors.toList()));
        group.setLogicType(item.getLogic());
        return group;
    }

    public boolean createRule(CustomizedRuleVO vo) {
        List<AbstractInterestRuleItem> groups = vo.getInterestRule().stream()
                .map(this::parseInterestItem)
                .collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        CustomizedRule rule = CustomizedRule.builder()
                .id(UUID.randomUUID().toString())
                .crawlRule(vo.getCrawlRule())
                .interestRule(new InterestRule(groups))
                .createTime(now)
                .updateTime(now)
                .build();
        return Optional.ofNullable(repository.save(rule))
                .map(id -> Boolean.TRUE)
                .orElse(Boolean.FALSE);
    }
}
