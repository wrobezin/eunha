package io.github.wrobezin.eunha.crawler.data;

import io.github.wrobezin.eunha.crawler.entity.CrawlResult;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;

import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 19:43
 */
public interface ContentDataOperator {
    String NOT_UPDATED = "NOT_UPDATED";

    /**
     * 保存页面内容
     *
     * @param parseResult 页面解析结果
     * @return 爬取结果
     */
    CrawlResult savePageData(ParseResult parseResult);


    /**
     * 通过id从ES获取页面内容
     *
     * @param id id
     * @return 页面内容
     */
    Object getContentFromEsById(String id);

    /**
     * 通过url从ES获取页面内容
     *
     * @param url url
     * @return 页面内容
     */
    Object getContentFromEsByUrl(String url);

    /**
     * 根据关键词搜索页面内容
     *
     * @param keyword  关键词
     * @param page     页
     * @param pageSize 页大小
     * @return 页面内容列表
     */
    List<?> searchContent(String keyword, int page, int pageSize);
}
