package io.github.wrobezin.eunha.data.entity.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Objects;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/3/28 19:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HyperLink {
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String anchorText;

    @Field(type = FieldType.Keyword)
    private String url;

    public HyperLink(String url) {
        this.url = url;
        this.anchorText = "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAnchorText(), getUrl());
    }
}
