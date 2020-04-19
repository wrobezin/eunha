package io.github.wrobezin.eunha.data.entity.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 19:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document("page")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "eunha", shards = 1)
public class Page {
    @Id
    private String id;

    @Field
    private String host;

    @Field
    private String url;

    @Field
    private String title;

    @Field
    private String body;

    @Field
    private String fingerPrint;

    @Field
    private Integer version;

    @Field
    private List<HyperLink> hyperLinks;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    public static final Page BLANK = Page.builder()
            .id("")
            .url("")
            .title("")
            .body("")
            .fingerPrint("")
            .version(0)
            .host("")
            .hyperLinks(Collections.emptyList())
            .build();
}
