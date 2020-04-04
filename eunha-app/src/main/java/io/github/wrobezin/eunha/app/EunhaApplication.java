package io.github.wrobezin.eunha.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 19:05
 */
@SpringBootApplication(scanBasePackages = {"io.github.wrobezin.eunha","io.github.wrobezin.framework"},
        exclude = MongoRepositoriesAutoConfiguration.class)
@EnableMongoRepositories(basePackages = "io.github.wrobezin.eunha.data.repository.mongo")
@EnableElasticsearchRepositories(basePackages = "io.github.wrobezin.eunha.data.repository.elasticsearch")
public class EunhaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EunhaApplication.class, args);
    }
}
