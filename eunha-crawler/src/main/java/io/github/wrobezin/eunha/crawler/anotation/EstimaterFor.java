package io.github.wrobezin.eunha.crawler.anotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/6 19:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface EstimaterFor {
    @AliasFor("value")
    Class<?> entityType() default Object.class;

    @AliasFor("entityType")
    Class<?> value() default Object.class;
}
