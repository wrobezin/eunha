package io.github.wrobezin.eunha.data.entity.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/23 16:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id;
    private String title;
    private String content;
    private Map<String, String> params;
    private Boolean read;
}
