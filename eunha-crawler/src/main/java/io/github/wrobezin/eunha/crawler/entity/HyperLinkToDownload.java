package io.github.wrobezin.eunha.crawler.entity;

import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.eunha.data.entity.rule.InterestRule;
import lombok.Data;

/**
 * 待下载超链接
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/30 16:19
 */
@Data
public class HyperLinkToDownload {
    private Integer depth;
    private HyperLink link;
    private HyperLinkToDownload parent;
    private Double score;

    private static final Double SCORE_DEFAULT = 0.0;

    public HyperLinkToDownload() {
        this.score = SCORE_DEFAULT;
        this.depth = 0;
    }

    public HyperLinkToDownload(String url) {
        this();
        this.link = new HyperLink(url);
    }

    public HyperLinkToDownload(HyperLink link, HyperLinkToDownload parent, ParseResult parentParseResult, InterestRule interestRule) {
        this.depth = parent.getDepth() + 1;
        this.link = link;
        this.parent = parent;
    }
}
