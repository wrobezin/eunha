package io.github.wrobezin.eunha.crawler.data;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 19:43
 */
public interface ContentDataOperator {
    /**
     * 保存到MongoDB
     * @param content 页面内容
     * @return id
     */
    String saveMongo(Object content);

    /**
     * 保存到ES
     * @param content 页面内容
     * @return id
     */
    String saveElasticsearch(Object content);
}
