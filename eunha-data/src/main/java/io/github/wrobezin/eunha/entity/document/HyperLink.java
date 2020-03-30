package io.github.wrobezin.eunha.entity.document;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 19:45
 */
@Data
@Builder
@NoArgsConstructor
public class HyperLink {
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String anchorText;

    @Field(type = FieldType.Keyword)
    private String url;

    public HyperLink(String url) {
        this.url = url;
        this.anchorText = "";
    }
}
