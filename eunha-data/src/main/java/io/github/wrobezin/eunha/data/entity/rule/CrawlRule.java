package io.github.wrobezin.eunha.data.entity.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
@AllArgsConstructor
public class CrawlRule {
    /** 种子URL */
    private String seedUrl;

    /** 要获取的xpath，截取后按顺序拼接在一起；为空时取body */
    private List<String> xpath;

    /** 是否扩展到其它页面 */
    private Boolean expandable;

    /** 是否扩展到其它网站，若expandable为false则该项失效 */
    private Boolean expandToOtherSite;

    /** 最大扩展深度 */
    private Integer maxExpandDepth;

    public static final Integer EXPAND_DEPTH_INFINITE = Integer.MAX_VALUE;
    public static final Integer EXPAND_DEPTH_NONE = 0;
    public static final Integer EXPAND_DEPTH_DEFAULT = 100;
}
