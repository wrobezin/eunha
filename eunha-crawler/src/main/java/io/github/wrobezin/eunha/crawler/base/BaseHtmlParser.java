package io.github.wrobezin.eunha.crawler.base;

import io.github.wrobezin.eunha.crawler.base.entity.DownloadResult;
import io.github.wrobezin.eunha.crawler.base.entity.ParseResult;
import io.github.wrobezin.eunha.crawler.base.utils.HyperLinkUtils;
import io.github.wrobezin.eunha.entity.document.HyperLink;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 默认的通用HTML解析器
 * TODO 支持GBK、GB2312等编码
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/31 3:26
 */
@Slf4j
@Component
public class BaseHtmlParser {
    public ParseResult<String> parse(DownloadResult downloadResult) {
        ParseResult<String> parseResult = new ParseResult<>();
        String html = Optional.ofNullable(downloadResult)
                .map(DownloadResult::getResponse)
                .map(Response::body)
                .map(responseBody -> {
                    try {
                        return responseBody.string();
                    } catch (IOException e) {
                        log.error("获取响应载荷字符串错误", e);
                        return "";
                    }
                }).orElse("");
        Document document = Jsoup.parse(html);
        Element body = document.getElementById("body");
        List<HyperLink> hyperLinks = HyperLinkUtils.getAllLinks(body);
        parseResult.setUrl(Optional.ofNullable(downloadResult).map(DownloadResult::getUrlInfo).map(UrlInfo::getUrl).orElse(""));
        parseResult.setContent(parseBody(body));
        parseResult.setLinks(hyperLinks);
        return parseResult;
    }

    protected String parseBody(Element body) {
        return body.toString();
    }
}
