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
    // TODO 扫包注册
    private Map<String, AbstractParser> parserMap;

    private GeneralHtmlParser generalHtmlParser;

    public ParserRouter(GeneralHtmlParser generalHtmlParser) {
        this.parserMap = new HashMap<>(16);
        this.generalHtmlParser = generalHtmlParser;
    }

    public ParseResult parse(DownloadResult downloadResult) {
        return parserMap.getOrDefault(downloadResult.getUrlInfo().getHost(), generalHtmlParser).parse(downloadResult);
    }
}
