package io.github.wrobezin.eunha.data.entity.document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.github.wrobezin.eunha.data.utils.EntityHashUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/3/29 1:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document("html")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "eunha-html", shards = 1)
public class OriginalDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String url;

    @Field(type = FieldType.Keyword)
    private String fingerPrint;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String body;

    @Field(type = FieldType.Date)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    private Integer version;

    public static final OriginalDocument BLANK = OriginalDocument.builder()
            .url("")
            .title("")
            .body("")
            .fingerPrint(EntityHashUtils.generateOriginalDocumentFingerPrint(new OriginalDocument()))
            .build();
}
