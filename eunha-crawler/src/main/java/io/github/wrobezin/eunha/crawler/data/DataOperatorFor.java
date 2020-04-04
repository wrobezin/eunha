package io.github.wrobezin.eunha.crawler.data;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/4 16:49
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface DataOperatorFor {
    @AliasFor("forType")
    Class<?> value() default Object.class;

    @AliasFor("value")
    Class<?> forType() default Object.class;
}
