package io.github.wrobezin.eunha.data.enums;

import lombok.Getter;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/19 22:22
 */
@Getter
public enum PushContactTypeEnum {
    /** 邮件 */
    EMAIL,
    /** 钉钉机器人 */
    DING_TALK,
    /** WebHook */
    WEB_HOOK,
    /** 短信 */
    SMS
}
