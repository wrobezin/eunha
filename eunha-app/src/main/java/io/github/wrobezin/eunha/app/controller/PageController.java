package io.github.wrobezin.eunha.app.controller;

import io.github.wrobezin.eunha.app.service.PageService;
import io.github.wrobezin.eunha.data.entity.document.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/12 2:19
 */
@RestController
@RequestMapping("/page")
public class PageController {
    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping("/rule-matched")
    public List<Page> getRuleMatchedPages(String ruleId, Integer pageIndex, Integer pageSize) {
        return pageService.getMatchingRule(ruleId, pageIndex, pageSize);
    }

    @GetMapping("/rule-matched-count/{ruleId}")
    public Integer countRuleMatchedPages(@PathVariable String ruleId) {
        return pageService.countMatchingRule(ruleId);
    }
}
