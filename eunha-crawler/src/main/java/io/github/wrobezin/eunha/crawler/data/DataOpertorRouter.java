package io.github.wrobezin.eunha.crawler.data;

import io.github.wrobezin.framework.utils.spring.BeanUtils;
import io.github.wrobezin.framework.utils.spring.PackageScanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:45
 */
@Component
public class DataOpertorRouter {
    private Map<Class<?>, DataOperator> operatorMap;

    public DataOpertorRouter() {
        this.operatorMap = new HashMap<>(4);
        PackageScanUtils.classScan("io.github.wrobezin.eunha.crawler.data")
                .stream()
                .filter(c -> c.isAnnotationPresent(DataOperatorFor.class))
                .forEach(c -> {
                    Optional.ofNullable(AnnotationUtils.findAnnotation(c, DataOperatorFor.class))
                            .map(DataOperatorFor::value)
                            .ifPresent(entityClass -> operatorMap.put(entityClass, (DataOperator) BeanUtils.getBean(c)));
                });
        operatorMap.forEach((k, v) -> System.out.println(k + " " + v));
    }

//    public CrawlResult savePageData(ParseResult parseResult) {
//        return null;
//    }
}
