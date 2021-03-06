package io.github.wrobezin.eunha.crawler;

import io.github.wrobezin.eunha.crawler.entity.DownloadResult;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.crawler.utils.HyperLinkUtils;
import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.eunha.data.entity.rule.CrawlRule;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 通用HTML解析器
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/31 21:44
 */
@Component
@Slf4j
public class GeneralHtmlParser {
    private static final Cleaner HTML_CLEANER = new Cleaner(Whitelist.relaxed());
    private static final String XPATH_ROOT_HTML_PATTERN = "^/html";
    private static final Pattern CHARSET_PATTERN = Pattern.compile("<meta.*?charset=\"?(.*?)\"");
    private static final String DEFAULT_CHARSET = "UTF-8";

    private boolean isTextType(String type) {
        return type.toLowerCase().contains("text") || type.toLowerCase().contains("json");
    }

    /**
     * 获取请求载荷，默认取字符串形式
     *
     * @param response
     * @return
     */
    private String getPayload(Response response) {
        String result = "";
        ResponseBody body = response.body();
        if (isTextType(body.contentType().type())) {
            try {
                byte[] bytes = body.bytes();
                String pageContext = new String(bytes);
                Matcher matcher = CHARSET_PATTERN.matcher(pageContext);
                String charset = matcher.find() ? matcher.group(1).toUpperCase() : DEFAULT_CHARSET;
                result = new String(bytes, charset);
            } catch (IOException e) {
                log.error("获取请求体出错 {}", response.request().url(), e);
            }
        }
        body.close();
        return result;
    }

    private void transformImgSrc(Document document, UrlInfo urlInfo) {
        document.getElementsByTag("img")
                .forEach(a -> a.attr("src", HyperLinkUtils.transformToAbsoluteUrl(a.attr("src"), urlInfo)));
    }

    private void transformLinkHref(Document document, UrlInfo urlInfo) {
        document.getElementsByTag("a")
                .forEach(a -> a.attr("href", HyperLinkUtils.transformToAbsoluteUrl(a.attr("href"), urlInfo)));
    }

    private String getContentByXpath(Document document, List<String> xpath, UrlInfo urlInfo) {
        try {
            return xpath.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(JXDocument.create(document)::selN)
                    .map(nodes -> nodes.stream()
                            .map(JXNode::asElement)
                            .map(Objects::toString)
                            .collect(Collectors.joining())
                    ).collect(Collectors.joining());
        } catch (Exception e) {
            return "";
        }
    }

    private String getContentByBody(Document document, UrlInfo urlInfo) {
        return HTML_CLEANER.clean(document).body()
                .children()
                .stream()
                .map(Objects::toString)
                .collect(Collectors.joining());
    }

    private String getTitleByXpah(Document document, String xpath) {
        xpath = xpath.replaceAll(XPATH_ROOT_HTML_PATTERN, "/");
        try {
            return JXDocument.create(document).selNOne(xpath).asElement().text();
        } catch (Exception e) {
            return document.title();
        }
    }

    public ParseResult parse(DownloadResult downloadResult, CrawlRule rule) {
        UrlInfo urlInfo = downloadResult.getUrlInfo();
        Document document = Jsoup.parse(getPayload(downloadResult.getResponse()));
        transformLinkHref(document, urlInfo);
        transformImgSrc(document, urlInfo);
        String title = StringUtils.isNotBlank(rule.getTitleXpath())
                ? getTitleByXpah(document, rule.getTitleXpath())
                : document.title();
        String body = rule.getBodyXpath().size() > 0
                ? getContentByXpath(document, rule.getBodyXpath(), urlInfo)
                : getContentByBody(document, urlInfo);
        List<HyperLink> links = HyperLinkUtils.getAllAbsoluteHttpLinks(document);
        return ParseResult.builder()
                .urlInfo(urlInfo)
                .title(title)
                .body(body)
                .links(links)
                .build();
    }
}
