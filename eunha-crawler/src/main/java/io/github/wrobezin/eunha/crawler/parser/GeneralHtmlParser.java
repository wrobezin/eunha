package io.github.wrobezin.eunha.crawler.parser;

import io.github.wrobezin.eunha.data.entity.document.OriginalDocument;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 通用HTML解析器
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/31 21:44
 */
@Component
public class GeneralHtmlParser extends AbstractParser {
    private static final Cleaner HTML_CLEANER = new Cleaner(Whitelist.relaxed());

    @Override
    protected Object parseContent(UrlInfo urlInfo, Document document) {
        String title = document.title();
        document = HTML_CLEANER.clean(document);
        return OriginalDocument.builder()
                .url(urlInfo.getBaseUrl())
                .title(title)
                .body(document.body()
                        .children()
                        .stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining()))
                .build();
    }

    @Override
    protected Class<?> getContentType() {
        return OriginalDocument.class;
    }
}
