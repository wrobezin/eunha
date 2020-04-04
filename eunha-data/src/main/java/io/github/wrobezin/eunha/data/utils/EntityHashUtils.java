package io.github.wrobezin.eunha.data.utils;

import io.github.wrobezin.eunha.data.entity.document.Article;
import io.github.wrobezin.eunha.data.entity.document.OriginalDocument;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 20:34
 */
public final class EntityHashUtils {
    public static String generateUrlHash(String url) {
        return DigestUtils.sha256Hex(url);
    }

    public static String generateArticleFingerPrint(Article article) {
        return DigestUtils.sha256Hex(Stream.of(
                article.getAuthor(),
                article.getTitle(),
                article.getBody(),
                article.getPublishTime())
                .map(obj -> Optional.ofNullable(obj).map(Objects::toString).orElse(""))
                .map(DigestUtils::sha1Hex).collect(Collectors.joining()));
    }

    public static String generateOriginalFingerPrint(OriginalDocument document) {
        return DigestUtils.sha256Hex(Stream.of(
                document.getTitle(),
                document.getBody())
                .map(str -> Optional.ofNullable(str).orElse(""))
                .map(DigestUtils::sha1Hex).collect(Collectors.joining()));
    }
}
