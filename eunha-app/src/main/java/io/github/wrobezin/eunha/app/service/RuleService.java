package io.github.wrobezin.eunha.app.service;

import io.github.wrobezin.eunha.app.vo.CustomizedRuleVO;
import io.github.wrobezin.eunha.data.entity.rule.*;
import io.github.wrobezin.eunha.data.repository.mongo.CustomizedRuleMongoRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
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
        vo.setPushContacts(rule.getPushContacts());
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
        List<PushContact> pushContacts = Optional.ofNullable(vo.getPushContacts())
                .orElse(Collections.emptyList());
        pushContacts.forEach(contact -> {
            if (null == contact.getParams()) {
                contact.setParams(Collections.emptyMap());
            }
        });
        return CustomizedRule.builder()
                .id(vo.getId())
                .name(vo.getName())
                .crawlRule(vo.getCrawlRule())
                .interestRule(new InterestRule(groups))
                .createTime(now)
                .updateTime(now)
                .pushContacts(pushContacts)
                .build();
    }

    public boolean save(CustomizedRuleVO vo) {
        return Optional.ofNullable(vo)
                .map(this::voToDbEntity)
                .map(repository::save)
                .map(id -> Boolean.TRUE)
                .orElse(Boolean.FALSE);
    }

    public Integer countAll(String name) {
        return repository.countByNameLike(name);
    }

    public List<CustomizedRule> findRules() {
        return repository.findAll();
    }

    public List<CustomizedRule> findRules(String name, int pageIndex, int pageSize) {
        return repository.findByNameLike(name, PageRequest.of(pageIndex, pageSize)).toList();
    }

    public List<CustomizedRuleVO> queryVo(String name, int pageIndex, int pageSize) {
        return findRules(name, pageIndex, pageSize)
                .stream()
                .map(this::dbEntityTovo)
                .collect(Collectors.toList());
    }

    public CustomizedRuleVO queryVoById(String id) {
        return repository.findById(id)
                .map(this::dbEntityTovo)
                .orElse(null);
    }

    public boolean remove(String id) {
        boolean exists = repository.findById(id).isPresent();
        if (exists) {
            repository.deleteById(id);
        }
        return exists;
    }

    public int remove(List<String> idList) {
        List<CustomizedRule> rules = idList.stream()
                .map(repository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        rules.forEach(repository::delete);
        return rules.size();
    }
}
