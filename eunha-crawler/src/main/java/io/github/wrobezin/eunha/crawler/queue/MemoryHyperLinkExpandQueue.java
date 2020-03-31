package io.github.wrobezin.eunha.crawler.queue;

import io.github.wrobezin.eunha.crawler.entity.HyperLinkToDownload;
import io.github.wrobezin.framework.utils.http.HttpUrlUtils;

import javax.validation.constraints.NotNull;
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
    //    public class MemoryHyperLinkExpandQueue implements HyperLinkExpandQueue {
    private static final String URL_SEPARATION_TOKEN = "/";

    private PriorityQueue<HyperLinkToDownload> queue;

    /** 用于记录已访问的URL */
    private Set<String> visited;

    private static Integer getPathDepth(@NotNull final HyperLinkToDownload link) {
        return HttpUrlUtils.parseUrl(link.getLink().getUrl()).getPath().split(URL_SEPARATION_TOKEN).length;
    }

    public MemoryHyperLinkExpandQueue() {
        this.queue = new PriorityQueue<>(Comparator.comparing(MemoryHyperLinkExpandQueue::getPathDepth));
        this.visited = new HashSet<>(64);
    }

    @Override
    public boolean offer(HyperLinkToDownload hyperLink) {
        if (!this.visited.contains(hyperLink.getLink().getUrl())) {
            this.queue.offer(hyperLink);
            this.visited.add(hyperLink.getLink().getUrl());
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
