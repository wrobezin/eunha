package io.github.wrobezin.eunha.crawler.data;

import io.github.wrobezin.eunha.crawler.entity.ParseResult;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 19:43
 */
public interface ContentDataOperator {
    /**
     * 保存到MongoDB
     * @param parseResult 解析结果
     * @return id
     */
    String saveMongo(ParseResult parseResult);

    /**
     * 保存到ES
     * @param parseResult 解析结果
     * @return id
     */
    String saveElasticsearch(ParseResult parseResult);
}
