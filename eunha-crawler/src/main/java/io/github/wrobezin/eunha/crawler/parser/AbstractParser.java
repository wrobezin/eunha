package io.github.wrobezin.eunha.crawler.parser;

import io.github.wrobezin.eunha.crawler.entity.DownloadResult;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.crawler.utils.HyperLinkUtils;
import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 抽象解析器
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/31 21:16
 */
@Slf4j
public abstract class AbstractParser {
    /**
     * 解析
     *
     * @param downloadResult 下载结果
     * @return 解析结果
     */
    public ParseResult parse(DownloadResult downloadResult) {
        ParseResult parseResult = new ParseResult();
        Document document = Jsoup.parse(getPayload(downloadResult.getResponse()));
        List<HyperLink> hyperLinks = HyperLinkUtils.getAllLinks(document);
        UrlInfo urlInfo = downloadResult.getUrlInfo();
        parseResult.setUrl(urlInfo.getUrl());
        parseResult.setContent(parseContent(urlInfo, document));
        parseResult.setContentType(getContentType());
        parseResult.setLinks(hyperLinks);
        return parseResult;
    }

    /**
     * 获取请求载荷，默认取字符串形式
     * TODO 支持GBK等字符集
     *
     * @param response
     * @return
     */
    protected String getPayload(Response response) {
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

    /**
     * 解析内容
     *
     * @param urlInfo  URL信息
     * @param document HTML
     * @return 解析结果
     */
    protected abstract Object parseContent(UrlInfo urlInfo, Document document);

    /**
     * 获取内容类型
     *
     * @return 页面内容类型
     */
    protected abstract Class<?> getContentType();
}
