package io.github.wrobezin.eunha.crawler.utils;

import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.framework.utils.http.HttpUrlUtils;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
    private static final String RELATIVE_PATH_FLAG = "/";

    public static List<HyperLink> getAllLinks(Element element, String protocal, String host) {
        return getLinksSatisfy(element, url -> true, url -> {
            // 把相对路径转换成绝对路径
            if (url.startsWith(RELATIVE_PATH_FLAG)) {
                return protocal + "://" + host + url;
            }
            return url;
        });
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
        String html = "<div class=\"cat-excerpt clearfloat subfeature\">\n" +
                "\t\t\t\t\t<a href=\"http://jiaren.org/2017/06/15/zaoan-1045/\" rel=\"bookmark\"\n" +
                "\t\t\t\t\t\ttitle=\"Permanent Link to 早安心语：远离消耗你的人，也不去消耗别人\"\n" +
                "\t\t\t\t\t\ttarget=\"_blank\"><img width=\"70\" height=\"70\" src=\"http://pic.jiaren.org/wp-pic/2017/06/c21e5ec58cd27287e8eb92289772fb44906feabbcfcf8-Mts40z_fw658-70x70.jpeg\" class=\"attachment-archive-small\" alt=\"c21e5ec58cd27287e8eb92289772fb44906feabbcfcf8-Mts40z_fw658\" /></a>\n" +
                "\t\t\t\t\t\t<h4><a href=\"http://jiaren.org/2017/06/15/zaoan-1045/\" rel=\"bookmark\"\n" +
                "\t\t\t\t\t\t\t\ttitle=\"早安心语：远离消耗你的人，也不去消耗别人\" target=\"_blank\">早安心语：远离消耗你的人，也不去消耗别人</a></h4>\n" +
                "\t\t\t\t\t\t<p>早安心语：你并不内向，只是不想搭理在你心中不重要的人。更多美文请关注佳人官方微信号：佳人（ID：jiarenorg） ， [&hellip;]</p>\n" +
                "\t\t\t\t</div>";
        Document parse = Jsoup.parse(html);
        parse.getElementsByTag("a").forEach(System.out::println);
    }
}
