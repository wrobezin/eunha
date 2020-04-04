package io.github.wrobezin.eunha.data.entity.document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.github.wrobezin.eunha.data.enums.ArticleCatrgoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Field
    private List<Integer> categories;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String body;

    @Field(type = FieldType.Date)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime publishTime;

    public boolean isNotBlank() {
        return Optional.of(getCategories())
                .map(categories -> categories.size() != 1 || !categories.get(0).equals(ArticleCatrgoryEnum.BLANK.getCode()))
                .orElse(false);
    }

    public String generateFingerPrint() {
        String infoFingerPrints = Stream.of(getTitle(), getAuthor(), getPublishTime(), getBody())
                .map(obj -> Optional.ofNullable(obj).map(Object::toString).orElse(""))
                .map(DigestUtils::md5Hex)
                .collect(Collectors.joining());
        return DigestUtils.sha256Hex(infoFingerPrints);
    }

    public static final Article BLANK = Article.builder()
            .title("")
            .author("")
            .categories(Collections.singletonList(ArticleCatrgoryEnum.BLANK.getCode()))
            .body("")
            .url("")
            .fingerPrint(new Article().generateFingerPrint())
            .build();
}
