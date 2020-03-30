package io.github.wrobezin.eunha.entity.rule;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抓取规则
 *
 * @author yuan
 * @version 1.0
 * @date 2020/3/30 13:52
 */
@Data
@Builder
@NoArgsConstructor
public class CrawlRule {
    /** 种子URL */
    private String seedUrl;

    /** 是否扩展到其它页面 */
    private Boolean expandable;

    /** 是否扩展到其它网站，若expandable为false则该项失效 */
    private Boolean expandToOtherSite;

    /** 最大扩展深度 */
    private Integer maxExpandDepth;

    public static final Integer EXPAND_DEPTH_INFINITE = -1;
    public static final Integer EXPAND_DEPTH_ZERO = 0;
    public static final Integer EXPAND_DEPTH_DEFAULT = 100;
}
