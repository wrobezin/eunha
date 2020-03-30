package io.github.wrobezin.eunha.crawler.base.queue;


import io.github.wrobezin.eunha.crawler.base.entity.HyperLinkToDownload;

/**
 * 超链接扩展队列
 *
 * @author yuan
 * @version 0.1
 * @apiNote 扩展新链接时应注意排除一定时间段内已抓取过的URL
 * @date 2020/3/30 15:35
 */
public interface HyperLinkExpandQueue {
    /**
     * 增加超链接
     *
     * @param link 超链接
     * @return 是否增加成功
     */
    boolean offer(HyperLinkToDownload link);

    /**
     * 获取下一个超链接
     *
     * @return 下一个超链接
     */
    HyperLinkToDownload poll();

    /**
     * 判断队列是否为空
     *
     * @return 是否为空
     */
    boolean isEmpty();
}
