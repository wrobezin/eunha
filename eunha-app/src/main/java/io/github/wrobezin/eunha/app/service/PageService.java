package io.github.wrobezin.eunha.app.service;

import io.github.wrobezin.eunha.data.entity.document.CompatibilityScore;
import io.github.wrobezin.eunha.data.entity.document.Page;
import io.github.wrobezin.eunha.data.repository.elasticsearch.PageElasticsearchRepository;
import io.github.wrobezin.eunha.data.repository.mongo.CompatibilityScoreMongoRepository;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/14 16:30
 */
@Service
public class PageService {
    private final CompatibilityScoreMongoRepository compatibilityRepository;
    private final PageElasticsearchRepository pageEsRepository;

    public PageService(CompatibilityScoreMongoRepository compatibilityRepository, PageElasticsearchRepository pageEsRepository) {
        this.compatibilityRepository = compatibilityRepository;
        this.pageEsRepository = pageEsRepository;
    }

    public List<Page> getMatchingRule(String ruleId, int pageIndex, int pageSize) {
        return compatibilityRepository.findByRuleIdAndValueGreaterThanEqual(ruleId, 1.0, PageRequest.of(pageIndex, pageSize))
                .stream()
                .map(CompatibilityScore::getUrl)
                .map(QueryParser::escape)
                .map(pageEsRepository::findByUrl)
                .peek(page -> System.out.println(page.getUrl()))
                .collect(Collectors.toList());
    }
}
