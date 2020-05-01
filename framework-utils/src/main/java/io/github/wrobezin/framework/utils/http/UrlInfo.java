package io.github.wrobezin.framework.utils.http;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 绝对路径URL信息
 *
 * @author yuan
 * date: 2020/1/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlInfo {
    @JSONField(ordinal = 1)
    private String protocal;
    @JSONField(ordinal = 2)
    private String host;
    @JSONField(ordinal = 3)
    private Integer port;
    @JSONField(ordinal = 4)
    private String path;
    @JSONField(ordinal = 5)
    private String queryString;
    @JSONField(ordinal = 6)
    private Map<String, String> queryMap;
    @JSONField(ordinal = 7)
    private String fragment;

    public String colonAndPort() {
        return getPort() == 80
                ? ""
                : ":" + getPort();
    }

    public String getBaseUrl() {
        return getProtocal() + "://" + getHost() + colonAndPort() + getPath();
    }

    public String getBaseUrlWithoutProtocal() {
        return getHost() + colonAndPort() + getPath();
    }

    public String getUrlWithQuery() {
        String baseUrl = getBaseUrl();
        String queryString = getQueryString();
        if (!StringUtils.isBlank(queryString)) {
            baseUrl += "?" + queryString;
        }
        return baseUrl;
    }

    public String getHostUrl() {
        return this.protocal + "://" + this.host + colonAndPort() + "/";
    }

    public static final UrlInfo BLANK = UrlInfo.builder()
            .protocal("")
            .host("")
            .port(80)
            .path("")
            .queryString("")
            .queryMap(new HashMap<>(0))
            .fragment("")
            .build();

    public boolean isHttpUrl() {
        return this.getProtocal().startsWith("http");
    }

    public UrlInfo upperPath() {
        String[] layouts = this.path.endsWith("/")
                ? this.path.substring(0, this.path.length() - 1).split("/")
                : this.path.split("/");
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < layouts.length - 1; i++) {
            path.append("/").append(layouts[i]);
        }
        return UrlInfo.builder()
                .protocal(this.protocal)
                .host(this.host)
                .port(this.port)
                .path(path.toString())
                .queryString("")
                .queryMap(Collections.emptyMap())
                .fragment("")
                .build();
    }
}
