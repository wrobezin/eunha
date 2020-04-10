package io.github.wrobezin.eunha.data.entity.document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.github.wrobezin.eunha.data.enums.ArticleCatrgoryEnum;
import io.github.wrobezin.eunha.data.utils.EntityHashUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 19:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "eunha-article", shards = 1)
public class Article {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String url;

    @Field(type = FieldType.Keyword)
    private String fingerPrint;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Keyword)
    private String author;

    /** 分类，一篇文章可能属于多个类别 */
    @Field(type = FieldType.Object)
    private List<Integer> categories;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String body;

    @Field(type = FieldType.Date)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime publishTime;

    @Field(type = FieldType.Date)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    private Integer version;

    public boolean isNotBlank() {
        return Optional.of(getCategories())
                .map(categories -> categories.size() != 1 || !categories.get(0).equals(ArticleCatrgoryEnum.BLANK.getCode()))
                .orElse(false);
    }

    public static final Article BLANK = Article.builder()
            .title("")
            .author("")
            .categories(Collections.singletonList(ArticleCatrgoryEnum.BLANK.getCode()))
            .body("")
            .url("")
            .fingerPrint(EntityHashUtils.generateArticleFingerPrint(new Article()))
            .version(0)
            .build();
}
