package io.github.wrobezin.eunha.data.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 20:34
 */
public final class EntityHashUtils {
    public static String generateUrlHash(String url) {
        return DigestUtils.sha256Hex(url);
    }

    public static String generatePageFingerPrint(String title, String body) {
        return DigestUtils.sha256Hex(title + body);
    }

    public static String generateMongoId(String fingerPrint, Integer version, LocalDateTime updateTime) {
        return DigestUtils.sha256Hex(fingerPrint + version + updateTime);
    }

    public static String generateCompatibilityId(String url, String ruleId) {
        return DigestUtils.sha256Hex(url + ruleId);
    }
}
