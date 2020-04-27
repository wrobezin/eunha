package io.github.wrobezin.eunha.crawler.utils;

import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.framework.utils.http.HttpUrlUtils;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yuan
 * date: 2020/1/21
 */
public final class HyperLinkUtils {
    private static final String ABSOLUTE_PATH_PREFIX = "//";
    private static final String ROOT_PATH_PREFIX = "/";
    private static final String SMAE_LAYOUT_PATH_PREFIX = "./";
    private static final String UPPER_LAYOUT_PATH_PREFIX = "../";
    private static final String INDEX_HTML_PATTERN = "/index(_\\d+)?.html?";

    private static String cleanDoubleSlash(String url) {
        while (url.contains("//")) {
            url = url.replace("//", "/");
        }
        return url;
    }

    public static String transformToAbsoluteUrl(String url, UrlInfo parentUrl) {
        if (url.startsWith(ABSOLUTE_PATH_PREFIX)) {
            return parentUrl.getProtocal() + ":" + url;
        } else if (url.startsWith(ROOT_PATH_PREFIX)) {
            return parentUrl.getHostUrl() + url.substring(1);
        } else if (url.startsWith(SMAE_LAYOUT_PATH_PREFIX)) {
            url = parentUrl.getBaseUrlWithoutProtocal().replaceAll(INDEX_HTML_PATTERN, "") + "/" + url.substring(1);
            return parentUrl.getProtocal() + "://" + cleanDoubleSlash(url);
        } else if (url.startsWith(UPPER_LAYOUT_PATH_PREFIX)) {
            while (url.startsWith(UPPER_LAYOUT_PATH_PREFIX)) {
                parentUrl = parentUrl.upperPath();
                url = url.substring(3);
            }
            return parentUrl.getProtocal() + "://" + cleanDoubleSlash(parentUrl.getBaseUrlWithoutProtocal() + "/" + url);
        } else if (!url.startsWith("http")) {
            String baseUrl = parentUrl.getBaseUrl();
            return baseUrl.endsWith("/")
                    ? baseUrl + url
                    : baseUrl + "/" + url;
        }
        return url;
    }

    public static List<HyperLink> getAllLinks(Element element, UrlInfo parentUrl) {
        return getLinksSatisfy(element, url -> true, url -> transformToAbsoluteUrl(url, parentUrl));
    }

    public static List<HyperLink> getAllAbsoluteHttpLinks(Element element) {
        return getLinksSatisfy(element, UrlInfo::isHttpUrl, url -> url);
    }

    public static List<HyperLink> getLinksSatisfy(Element element, Predicate<UrlInfo> satisfy, Function<String, String> urlMapper) {
        return Optional.ofNullable(element)
                .map(e -> e.getElementsByTag("a"))
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(aTag -> {
                    String title = aTag.attr("title");
                    String text = aTag.text();
                    String anchorText = StringUtils.isBlank(title) ? text : title;
                    String url = Optional.ofNullable(aTag.attr("href"))
                            .map(HyperLinkUtils::cutWhiteToken)
                            .map(urlMapper)
                            .orElse("");
                    return new HyperLink(anchorText, url);
                })
                .filter(link -> satisfy.test(HttpUrlUtils.parseUrl(link.getUrl())))
                .collect(Collectors.toList());
    }

    /**
     * 把带有未进行URL编码空白符的URL切割开，取最前面的一段
     * 用于处理某些网页href标签的双引号没有闭合导致解析错误的问题
     * 例：<a href="http://ka.sina.cm.cn/index/mmo\r\ntarget="_blank">网络游戏礼包</a>
     *
     * @param url 原始URL
     * @return 截取空白符前的部分
     */
    private static String cutWhiteToken(String url) {
        return url.split("\\s")[0];
    }

    public static void main(String[] args) {
        UrlInfo url1 = HttpUrlUtils.parseUrl("http://www.whut.edu.cn/xxgk/xyfg/mqdy/");
        System.out.println(transformToAbsoluteUrl("../nhxqny/", url1));
        System.out.println(transformToAbsoluteUrl("../../nhxqny/", url1));
        System.out.println(transformToAbsoluteUrl("./nhxqny/", url1));
        System.out.println(transformToAbsoluteUrl("/nhxqny/", url1));
        System.out.println(transformToAbsoluteUrl("nhxqny/", url1));
        System.out.println(transformToAbsoluteUrl("//nhxqny.com/fuck", url1));
    }
}
