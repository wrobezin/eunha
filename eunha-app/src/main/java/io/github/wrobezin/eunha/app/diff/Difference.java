package io.github.wrobezin.eunha.app.diff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/7 18:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Difference {
    /** 差异类型（增删改） */
    private String changeType;
    /** 信息类型（标题、正文、作者等） */
    private String infoType;
    private Integer position;
    private Integer lineNum;

    public static final String TITLE = "title";
    public static final String BODY = "body";
}
