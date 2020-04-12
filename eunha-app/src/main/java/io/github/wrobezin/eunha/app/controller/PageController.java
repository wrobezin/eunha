package io.github.wrobezin.eunha.app.controller;

import io.github.wrobezin.eunha.crawler.data.DataOpertorWraper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/12 2:19
 */
@RestController
@RequestMapping("/page")
public class PageController {
    private final DataOpertorWraper dataOpertorWraper;

    public PageController(DataOpertorWraper dataOpertorWraper) {
        this.dataOpertorWraper = dataOpertorWraper;
    }

    @GetMapping("/content")
    public Object getContent(String contentType, String id) {
        return dataOpertorWraper.getContent(contentType, id);
    }
}
