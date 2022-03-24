package com.cudrania.core.pipeline;

import com.cudrania.core.functions.Fn.Lambda;
import lombok.SneakyThrows;

import java.lang.invoke.SerializedLambda;

/**
 * 命名函数
 *
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface Named<T, R> extends Lambda {
    R apply(T t);


    @SneakyThrows
    default String name() {
        SerializedLambda s = this.lambda();
        return Class.forName(s.getImplClass().replace('/', '.')).getName() + "#" + s.getImplMethodName();
    }


    static <P, R> Named<P, R> of(Named<P, R> lambda) {
        return lambda;
    }
}
