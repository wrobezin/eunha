package io.github.wrobezin.eunha.app.controller;

import io.github.wrobezin.eunha.app.service.RuleService;
import io.github.wrobezin.eunha.app.vo.CustomizedRuleVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public boolean createRule(@RequestBody CustomizedRuleVO rule) {
        return ruleService.save(rule);
    }

    @GetMapping
    public List<CustomizedRuleVO> queryAllRule(){
        return ruleService.queryAll();
    }

    @GetMapping("/{id}")
    public List<CustomizedRuleVO> getById(@PathVariable String id){
        return ruleService.queryAll();
    }
}
