package io.github.wrobezin.eunha.data.entity.rule;

import io.github.wrobezin.eunha.data.enums.PushContactTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/19 22:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushContact {
    private PushContactTypeEnum type;
    private String value;
    private Map<String, String> params;
}
