package io.github.wrobezin.eunha.app.service;

import io.github.wrobezin.eunha.app.vo.PageVO;
import io.github.wrobezin.eunha.data.entity.document.CompatibilityScore;
import io.github.wrobezin.eunha.data.entity.document.Page;
import io.github.wrobezin.eunha.data.repository.elasticsearch.PageElasticsearchRepository;
import io.github.wrobezin.eunha.data.repository.mongo.CompatibilityScoreMongoRepository;
import io.github.wrobezin.eunha.data.repository.mongo.PageMongoRepository;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/14 16:30
 */
@Service
public class PageService {
    private static final double MATCHED = 1.0;

    private final CompatibilityScoreMongoRepository compatibilityRepository;
    private final PageElasticsearchRepository pageEsRepository;
    private final PageMongoRepository pageMongoRepository;

    @Resource
    private ElasticsearchRestTemplate esTemplate;

    private static final String SEARCH_HIGHLIGHT_PRE_TAG = "<search-em>";
    private static final String SEARCH_HIGHLIGHT_POST_TAG = "</search-em>";

    public PageService(CompatibilityScoreMongoRepository compatibilityRepository, PageElasticsearchRepository pageEsRepository, PageMongoRepository pageMongoRepository) {
        this.compatibilityRepository = compatibilityRepository;
        this.pageEsRepository = pageEsRepository;
        this.pageMongoRepository = pageMongoRepository;
    }

    public List<PageVO> getMatchingRule(String ruleId, int pageIndex, int pageSize) {
        return compatibilityRepository.findByRuleIdAndValueGreaterThanEqual(ruleId, MATCHED, PageRequest.of(pageIndex, pageSize))
                .stream()
                .map(CompatibilityScore::getUrl)
                .map(pageEsRepository::findByUrl)
                .map(PageVO::new)
                .collect(Collectors.toList());
    }

    public Long countMatchingRule(String ruleId) {
        return compatibilityRepository.countByRuleIdAndValueGreaterThanEqual(ruleId, MATCHED);
    }

    private List<String> cutKeywords(String keywords) {
        if (StringUtils.isBlank(keywords)) {
            return Collections.emptyList();
        }
        return Arrays.stream(keywords.split("[\\p{P}\\s]"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    private NativeSearchQueryBuilder createKeywordQueryBuilder(List<String> keywords) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        keywords.forEach(word -> boolQuery.should(QueryBuilders.matchQuery("title", word)));
        keywords.forEach(word -> boolQuery.should(QueryBuilders.matchQuery("body", word)));
        queryBuilder.withQuery(boolQuery);
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("title")
                .field("body")
                .preTags(SEARCH_HIGHLIGHT_PRE_TAG)
                .postTags(SEARCH_HIGHLIGHT_POST_TAG)
                .numOfFragments(0);
        queryBuilder.withHighlightBuilder(highlightBuilder);
        return queryBuilder;
    }

    public Long countByKeywords(String keywords) {
        return esTemplate.count(createKeywordQueryBuilder(cutKeywords(keywords)).build(), Page.class);
    }

    public List<PageVO> searchByKeywords(String keywords, int pageIndex, int pageSize) {
        NativeSearchQueryBuilder keywordQuery = createKeywordQueryBuilder(cutKeywords(keywords));
        keywordQuery.withPageable(PageRequest.of(pageIndex, pageSize));
        SearchHits<Page> searchHits = esTemplate.search(keywordQuery.build(), Page.class);
        return StreamSupport.stream(searchHits.spliterator(), false)
                .map(hit -> {
                    Page page = hit.getContent();
                    Optional.ofNullable(hit.getHighlightField("title"))
                            .filter(strs -> strs.size() > 0)
                            .map(strs -> strs.get(0))
                            .ifPresent(page::setTitle);
                    Optional.ofNullable(hit.getHighlightField("body"))
                            .filter(strs -> strs.size() > 0)
                            .map(strs -> strs.get(0))
                            .ifPresent(page::setBody);
                    return page;
                })
                .map(PageVO::new)
                .collect(Collectors.toList());
    }

    public List<PageVO> getHistoryPages(String url) {
        return pageMongoRepository.findAllByUrlOrderByVersionDesc(url)
                .stream()
                .map(PageVO::new)
                .collect(Collectors.toList());
    }

    public Integer countHistoryPages(String url) {
        return pageMongoRepository.countByUrl(url);
    }
}
