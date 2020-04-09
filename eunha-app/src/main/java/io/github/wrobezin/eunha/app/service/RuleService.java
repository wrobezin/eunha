package io.github.wrobezin.eunha.app.service;

import io.github.wrobezin.eunha.app.vo.CustomizedRuleVO;
import io.github.wrobezin.eunha.data.entity.rule.*;
import io.github.wrobezin.eunha.data.repository.mongo.CustomizedRuleMongoRepository;
import org.apache.commons.lang.StringUtils;
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
        if (null == item.getSub() || item.getSub().isEmpty()) {
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

    private CustomizedRuleVO.InterestItem transformToVoItem(AbstractInterestRuleItem item) {
        CustomizedRuleVO.InterestItem voItem = new CustomizedRuleVO.InterestItem();
        voItem.setLogic(item.getLogicType());
        if (item instanceof SingleInterestRuleItem) {
            voItem.setType(((SingleInterestRuleItem) item).getJudgeType());
            voItem.setValue(((SingleInterestRuleItem) item).getValue());
        } else {
            List<CustomizedRuleVO.InterestItem> sub = ((InterestRuleItemGroup) item).getRuleItems().stream()
                    .map(this::transformToVoItem)
                    .collect(Collectors.toList());
            voItem.setSub(sub);
            voItem.setValue("");
        }
        return voItem;
    }

    private CustomizedRuleVO dbEntityTovo(CustomizedRule rule) {
        CustomizedRuleVO vo = new CustomizedRuleVO();
        vo.setId(rule.getId());
        vo.setCrawlRule(rule.getCrawlRule());
        vo.setName(rule.getName());
        vo.setInterestRule(rule.getInterestRule()
                .getInterestRules()
                .stream()
                .map(this::transformToVoItem)
                .collect(Collectors.toList()));
        return vo;
    }

    private CustomizedRule voToDbEntity(CustomizedRuleVO vo) {
        List<AbstractInterestRuleItem> groups = vo.getInterestRule().stream()
                .map(this::parseInterestItem)
                .collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        if (StringUtils.isBlank(vo.getId())) {
            vo.setId(UUID.randomUUID().toString());
        }
        return CustomizedRule.builder()
                .id(vo.getId())
                .name(vo.getName())
                .crawlRule(vo.getCrawlRule())
                .interestRule(new InterestRule(groups))
                .createTime(now)
                .updateTime(now)
                .build();
    }

    public boolean save(CustomizedRuleVO vo) {
        return Optional.ofNullable(vo)
                .map(this::voToDbEntity)
                .map(repository::save)
                .map(id -> Boolean.TRUE)
                .orElse(Boolean.FALSE);
    }

    public List<CustomizedRuleVO> queryAll() {
        return repository.findAll().stream()
                .map(this::dbEntityTovo)
                .collect(Collectors.toList());
    }

    public CustomizedRuleVO queryById(String id) {
        return repository.findById(id)
                .map(this::dbEntityTovo)
                .orElse(null);
    }
}
