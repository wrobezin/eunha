package io.github.wrobezin.eunha.data.entity.document;

import io.github.wrobezin.eunha.data.utils.EntityHashUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 记录规则与URL的对应评分
 *
 * @author yuan
 * @version 1.0
 * @date 2020/4/10 20:11
 */
@Data
@NoArgsConstructor
@Document(collection = "score")
public class CompatibilityScore {
    @Id
    private String id;
    private String url;
    private String ruleId;
    private Double value;

    public CompatibilityScore(String url, String ruleId, Double value) {
        this.url = url;
        this.ruleId = ruleId;
        this.value = value;
        this.id = EntityHashUtils.generateCompatibilityMongoId(url, ruleId);
    }
}
