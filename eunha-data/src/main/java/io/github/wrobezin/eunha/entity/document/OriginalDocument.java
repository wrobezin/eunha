package io.github.wrobezin.eunha.entity.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/3/29 1:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "eunha-html", shards = 1)
public class OriginalDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String url;

    @Field(type = FieldType.Keyword)
    private String fingerPrint;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String body;

    public static String getFingerPrint(String content) {
        return DigestUtils.sha256Hex(content);
    }

    public static final OriginalDocument BLANK = OriginalDocument.builder()
            .url("")
            .body("")
            .fingerPrint(getFingerPrint(""))
            .build();
}
