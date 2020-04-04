package io.github.wrobezin.eunha.crawler.utils;

import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.framework.utils.http.HttpUrlUtils;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import org.jsoup.nodes.Element;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yuan
 * date: 2020/1/21
 */
public final class HyperLinkUtils {
    public static List<HyperLink> getAllLinks(Element element) {
        return getLinksSatisfy(element, UrlInfo::isHttpUrl);
    }

    public static List<HyperLink> getLinksSatisfy(Element element, Predicate<UrlInfo> satisfy) {
        return Optional.ofNullable(element)
                .map(e -> e.getElementsByTag("a"))
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(aTag -> {
                    String description = Optional.ofNullable(aTag.text()).orElse("");
                    String url = Optional.ofNullable(aTag.attr("href"))
                            .map(HyperLinkUtils::cutWhiteToken)
                            .orElse("");
                    return new HyperLink(description, url);
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
}
