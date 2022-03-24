package com.cudrania.core.pipeline;

import com.cudrania.core.functions.Fn.Lambda;

/**
 * 命名函数
 *
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface Named<T, R> extends Lambda {
    R apply(T t);

    static <P, R> Named<P, R> of(Named<P, R> lambda) {
        return lambda;
    }
}
