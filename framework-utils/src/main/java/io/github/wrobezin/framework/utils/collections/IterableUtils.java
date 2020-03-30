package io.github.wrobezin.framework.utils.collections;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author yuan
 * @version 0.1
 * @date 2020/2/1
 */
public final class IterableUtils {
    public static <T> List<T> toList(Iterable<T> iterable, Supplier<? extends List<T>> supplier) {
        List<T> result = supplier.get();
        iterable.forEach(result::add);
        return result;
    }
}
