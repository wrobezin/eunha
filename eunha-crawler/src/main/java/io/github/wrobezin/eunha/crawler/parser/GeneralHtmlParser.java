package io.github.wrobezin.eunha.crawler.parser;

import io.github.wrobezin.eunha.data.entity.document.OriginalDocument;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * 通用HTML解析器
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/31 21:44
 */
@Component
public class GeneralHtmlParser extends AbstractParser {
    @Override
    protected Object parseContent(UrlInfo urlInfo, Document document) {
        return OriginalDocument.builder()
                .url(urlInfo.getUrl())
                .title(document.title())
                .body(document.body().text())
                .build();
    }

    @Override
    protected Class<?> getContentType() {
        return OriginalDocument.class;
    }
}
