package io.github.wrobezin.eunha.crawler.parser;

import io.github.wrobezin.eunha.crawler.entity.DownloadResult;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 解析器路由，根据下载结果获取对应解析器进行解析
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/31 21:16
 */
@Component
public class ParserRouter {
    // TODO 扫包注册专用解析器
    private Map<String, AbstractParser> specificParsers;
    private final GeneralHtmlParser generalHtmlParser;

    public ParserRouter(GeneralHtmlParser generalHtmlParser) {
        this.specificParsers = new HashMap<>(16);
        this.generalHtmlParser = generalHtmlParser;
    }

    public ParseResult parse(DownloadResult downloadResult) {
        String host = downloadResult.getUrlInfo().getHost();
        AbstractParser abstractParser = specificParsers.getOrDefault(host, generalHtmlParser);
        return abstractParser.parse(downloadResult);
    }
}
