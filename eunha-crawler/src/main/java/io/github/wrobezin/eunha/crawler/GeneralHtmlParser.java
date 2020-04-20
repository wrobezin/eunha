package io.github.wrobezin.eunha.crawler;

import io.github.wrobezin.eunha.crawler.entity.DownloadResult;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.crawler.utils.HyperLinkUtils;
import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    /**
     * 获取请求载荷，默认取字符串形式
     * TODO 支持GBK等字符集
     *
     * @param response
     * @return
     */
    private String getPayload(Response response) {
        return Optional.ofNullable(response)
                .map(Response::body)
                .map(responseBody -> {
                    try {
                        return responseBody.string();
                    } catch (IOException e) {
                        log.error("获取响应载荷字符串错误", e);
                        return "";
                    }
                }).orElse("");
    }

    public ParseResult parse(DownloadResult downloadResult) {
        UrlInfo urlInfo = downloadResult.getUrlInfo();
        Document document = Jsoup.parse(getPayload(downloadResult.getResponse()));
        String title = document.title();
        String body = HTML_CLEANER.clean(document).body()
                .children()
                .stream()
                .peek(element -> {
                    // 将图片的相对链接转换成绝对链接
                    if ("img".equals(element.tagName())) {
                        String src = element.attr("src");
                        if (StringUtils.isNotBlank(src) && src.startsWith("/")) {
                            src = urlInfo.getProtocal() + "://" + urlInfo.getHost() + src;
                            element.attr("src", src);
                        }
                    }
                })
                .map(Objects::toString)
                .collect(Collectors.joining());
        List<HyperLink> links = HyperLinkUtils.getAllLinks(document, urlInfo.getProtocal(), urlInfo.getHost());
        return ParseResult.builder()
                .urlInfo(urlInfo)
                .title(title)
                .body(body)
                .links(links)
                .build();
    }
}
