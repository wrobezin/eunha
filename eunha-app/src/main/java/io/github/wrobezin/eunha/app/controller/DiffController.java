package io.github.wrobezin.eunha.app.controller;

import io.github.wrobezin.eunha.app.diff.Difference;
import io.github.wrobezin.eunha.app.diff.DifferenceUtils;
import io.github.wrobezin.eunha.app.service.PageService;
import io.github.wrobezin.eunha.data.entity.document.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/22 16:08
 */
@RestController
@RequestMapping("/diff")
public class DiffController {
    private final PageService pageService;

    public DiffController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping
    public List<Difference> getDifferencees(String url, Integer version1, Integer version2) {
        Page page1 = pageService.getByUrlAndVersion(url, version1);
        Page page2 = pageService.getByUrlAndVersion(url, version2);
        return DifferenceUtils.diff(page1, page2);
    }
}
