package io.github.wrobezin.eunha.app.controller;

import io.github.wrobezin.eunha.app.service.RuleService;
import io.github.wrobezin.eunha.app.vo.CustomizedRuleVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/8 19:37
 */
@RestController
@RequestMapping("/rule")
public class RuleController {
    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping
    public boolean saveRule(@RequestBody CustomizedRuleVO rule) {
        return ruleService.save(rule);
    }

    @GetMapping
    public List<CustomizedRuleVO> queryAllRule(String name, Integer pageIndex, Integer pageSize) {
        return ruleService.queryVo(Optional.ofNullable(name).orElse(""), pageIndex, pageSize);
    }

    @GetMapping("/count")
    public Integer countAll(String name) {
        return ruleService.countAll(Optional.ofNullable(name).orElse(""));
    }

    @DeleteMapping("/{id}")
    public boolean deleteRule(@PathVariable String id) {
        return ruleService.remove(id);
    }

    @DeleteMapping("/")
    public Integer deleteRule(@RequestBody List<String> idList) {
        return ruleService.remove(idList);
    }
}
