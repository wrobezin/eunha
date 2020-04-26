package io.github.wrobezin.eunha.data.repository.mongo;

import io.github.wrobezin.eunha.data.entity.document.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/14 15:29
 */
public interface PageMongoRepository extends MongoRepository<Page, String> {

    /**
     * 根据URL获取最新版页面
     *
     * @param url URL
     * @return 最新版页面
     */
    Page findFirstByUrlOrderByVersionDesc(String url);

    /**
     * 根据URL获取各版本页面
     *
     * @param url URL
     * @return 各版本页面
     */
    List<Page> findAllByUrlOrderByVersionDesc(String url);

    /**
     * 统计URL对应页面的版本数量
     *
     * @param url URL
     * @return 页面版本数量
     */
    Integer countByUrl(String url);

    /**
     * 获取URL所对应页面的指定版本
     *
     * @param url     URL
     * @param version 版本
     * @return 指定页面
     */
    Page findByUrlAndVersion(String url, Integer version);

    /**
     * 根据URL删除页面
     *
     * @param url URL
     */
    void deleteByUrl(String url);

    /**
     * 根据标题获取页面
     *
     * @param title 标题
     * @return 页面
     */
    List<Page> findAllByTitle(String title);
}
