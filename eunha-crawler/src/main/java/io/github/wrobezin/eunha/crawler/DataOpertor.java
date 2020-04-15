package io.github.wrobezin.eunha.crawler;

import io.github.wrobezin.eunha.crawler.entity.CrawlResult;
import io.github.wrobezin.eunha.crawler.entity.ParseResult;
import io.github.wrobezin.eunha.data.entity.document.Page;
import io.github.wrobezin.eunha.data.repository.elasticsearch.PageElasticsearchRepository;
import io.github.wrobezin.eunha.data.repository.mongo.PageMongoRepository;
import io.github.wrobezin.eunha.data.utils.EntityHashUtils;
import io.github.wrobezin.framework.utils.http.UrlInfo;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/14 15:26
 */
@Component
public class DataOpertor {
    private final PageElasticsearchRepository pageEsRepository;

    private final PageMongoRepository pageMongoRepository;

    public DataOpertor(PageElasticsearchRepository pageEsRepository, PageMongoRepository pageMongoRepository) {
        this.pageEsRepository = pageEsRepository;
        this.pageMongoRepository = pageMongoRepository;
    }

    public List<Page> searchByKerword(List<String> keywords, int pageIndex, int pageSize) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        keywords.forEach(word -> boolQuery.should(QueryBuilders.matchQuery("title", word)));
        keywords.forEach(word -> boolQuery.should(QueryBuilders.matchQuery("body", word)));
        queryBuilder.withQuery(boolQuery);
        queryBuilder.withPageable(PageRequest.of(pageIndex, pageSize));
        return pageEsRepository.search(queryBuilder.build()).toList();
    }

    public CrawlResult savePageData(ParseResult parseResult) {
        String title = parseResult.getTitle();
        String body = parseResult.getBody();
        UrlInfo urlInfo = parseResult.getUrlInfo();
        String url = urlInfo.getUrlWithQuery();
        String fingerPrint = EntityHashUtils.generatePageFingerPrint(title, body);
        Page newestInDb = pageMongoRepository.findFirstByUrlOrderByVersionDesc(url);
        boolean newPage = null == newestInDb;
        boolean updated = newPage || !fingerPrint.equals(newestInDb.getFingerPrint());
        int version = ofNullable(newestInDb)
                .map(Page::getVersion)
                .map(v -> v + 1)
                .orElse(1);
        Page page = newestInDb;
        if (updated) {
            LocalDateTime now = LocalDateTime.now();
            page = Page.builder()
                    .id(EntityHashUtils.generateUrlHash(url))
                    .url(url)
                    .title(title)
                    .body(body)
                    .fingerPrint(fingerPrint)
                    .version(version)
                    .host(urlInfo.getHost())
                    .updateTime(now)
                    .hyperLinks(parseResult.getLinks())
                    .build();
            pageEsRepository.save(page);
            page.setId(EntityHashUtils.generateMongoId(fingerPrint, version, now));
            pageMongoRepository.save(page);
        }
        return CrawlResult.builder()
                .newPage(newPage)
                .updated(updated)
                .url(url)
                .version(version)
                .finishTime(LocalDateTime.now())
                .pageInDb(page)
                .build();
    }
}
