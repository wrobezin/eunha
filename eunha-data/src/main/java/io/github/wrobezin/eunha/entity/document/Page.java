package io.github.wrobezin.eunha.entity.document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 19:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "eunha-page", shards = 1)
public class Page {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String host;

    @Field(type = FieldType.Keyword)
    private String url;

    @Field(type = FieldType.Keyword)
    private Integer contentType;

    @Field(type = FieldType.Keyword)
    private String contentId;

    @Field
    private List<HyperLink> hyperLinks;

    @Field(type = FieldType.Date)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime crawlTime;

    @Field(type = FieldType.Keyword)
    private String fingerPrint;

    public String generateFingerPrint(String contentFingerPrint) {
        String linksFingerPrint = hyperLinks.stream()
                .map(link -> link.getAnchorText() + link.getAnchorText())
                .map(DigestUtils::md5Hex)
                .map(md5 -> md5.substring(0, 6))
                .collect(Collectors.joining());
        String fingerPrint = DigestUtils.sha256Hex(contentFingerPrint + linksFingerPrint);
        setFingerPrint(fingerPrint);
        return fingerPrint;
    }
}
