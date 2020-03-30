package io.github.wrobezin.eunha.crawler.base.entity;

import io.github.wrobezin.eunha.entity.document.HyperLink;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 待下载超链接
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/30 16:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HyperLinkToDownload {
    Integer depth;
    HyperLink link;
}
