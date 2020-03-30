package io.github.wrobezin.eunha.crawler.base.entity;

import io.github.wrobezin.framework.utils.http.UrlInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.Response;

/**
 * 下载结果
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/30 14:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadResult {
    UrlInfo urlInfo;
    Response response;
}
