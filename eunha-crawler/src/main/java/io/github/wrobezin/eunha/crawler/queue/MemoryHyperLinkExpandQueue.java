package io.github.wrobezin.eunha.crawler.queue;

import io.github.wrobezin.eunha.crawler.entity.HyperLinkToDownload;
import io.github.wrobezin.eunha.data.entity.document.HyperLink;
import io.github.wrobezin.framework.utils.http.HttpUrlUtils;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * 内存队列
 *
 * @author yuan
 * @version 0.1
 * @date 2020/3/30 16:50
 */
public class MemoryHyperLinkExpandQueue implements HyperLinkExpandQueue {
    private static final String URL_SEPARATION_TOKEN = "/";

    private PriorityQueue<HyperLinkToDownload> queue;

    /** 用于记录已在队的超链接 */
    private Set<HyperLink> inQueue;

    private static Integer getPathDepth(final HyperLinkToDownload link) {
        return HttpUrlUtils.parseUrl(link.getLink().getUrl()).getPath().split(URL_SEPARATION_TOKEN).length;
    }

    /**
     * 默认按URL的层级深度排列（浅链优先）
     */
    public MemoryHyperLinkExpandQueue() {
        this.queue = new PriorityQueue<>(Comparator.comparing(MemoryHyperLinkExpandQueue::getPathDepth));
        this.inQueue = new HashSet<>(64);
    }

    public MemoryHyperLinkExpandQueue(Comparator<HyperLinkToDownload> comparator) {
        this.queue = new PriorityQueue<>(comparator);
        this.inQueue = new HashSet<>(64);
    }

    @Override
    public boolean offer(HyperLinkToDownload hyperLink) {
        if (!this.inQueue.contains(hyperLink.getLink())) {
            this.queue.offer(hyperLink);
            this.inQueue.add(hyperLink.getLink());
            return true;
        }
        return false;
    }

    @Override
    public HyperLinkToDownload poll() {
        return this.queue.poll();
    }

    @Override
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }
}
