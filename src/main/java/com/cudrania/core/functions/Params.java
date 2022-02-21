package com.cudrania.core.functions;


import com.cudrania.core.arrays.ArrayUtils;
import com.cudrania.core.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 预定义{@link Param}对象
 *
 * @author scorpio
 * @version 1.0.0
 */
public class Params {

    /**
     * 判断对象不为null
     *
     * @param obj
     * @return
     */
    public static <T> ImmutableParam<T, T> notNull(T obj) {
        return with(obj).when(Objects::nonNull);
    }

    /**
     * 判断字符串不为空
     *
     * @param str
     * @return
     */
    public static ImmutableParam<String, String> notEmpty(String str) {
        return with(str, StringUtils::isNotEmpty);
    }


    /**
     * 判断集合不为空
     *
     * @param collection
     * @return
     */
    public static <T extends Collection<E>, E> ImmutableParam<T, T> notEmpty(T collection) {
        return with(collection, CollectionUtils::isNotEmpty);
    }


    /**
     * 判断数组不为空
     *
     * @param array
     * @return
     */
    public static <T> Param<T[]> notEmpty(T[] array) {
        return with(array, ArrayUtils::isNotEmpty);
    }


    /**
     * 判断等于指定数值
     *
     * @param number
     * @param other
     * @return
     */
    public static <T extends Number> ImmutableParam<T, T> eq(T number, int other) {
        return with(number).when(e -> e.intValue() == other);
    }


    /**
     * 判断等于0
     *
     * @param number
     * @return
     */
    public static <T extends Number> ImmutableParam<T, T> eq0(T number) {
        return eq(number, 0);
    }

    /**
     * 判断不等于指定数值
     *
     * @param number
     * @param other
     * @return
     */
    public static <T extends Number> ImmutableParam<T, T> ne(T number, int other) {
        return with(number).when(e -> e.intValue() != other);
    }


    /**
     * 判断不等于0
     *
     * @param number
     * @return
     */
    public static <T extends Number> ImmutableParam<T, T> ne0(T number) {
        return ne(number, 0);
    }

    /**
     * 判断参数大于指定数值
     *
     * @param number
     * @param other
     * @return
     */
    public static <T extends Number> ImmutableParam<T, T> gt(T number, int other) {
        return with(number).when(e -> e.intValue() > other);
    }


    /**
     * 判断参数大于0
     *
     * @param number
     * @return
     */
    public static <T extends Number> ImmutableParam<T, T> gt0(T number) {
        return gt(number, 0);
    }

    /**
     * 判断参数大于等于指定数值
     *
     * @param number
     * @param other
     * @return
     */
    public static <T extends Number> ImmutableParam<T, T> ge(T number, int other) {
        return with(number).when(e -> e.intValue() >= other);
    }

    /**
     * 判断参数大于等于指定数值
     *
     * @param number
     * @return
     */
    public static <T extends Number> ImmutableParam<T, T> ge0(T number) {
        return ge(number, 0);
    }

    /**
     * 判断参数小于指定数值
     *
     * @param number
     * @param other
     * @return
     */
    public static <T extends Number> ImmutableParam<T, T> lt(T number, int other) {
        return with(number).when(e -> e.intValue() < other);
    }

    /**
     * 判断参数小于0
     *
     * @param number
     * @return
     */
    public static <T extends Number> ImmutableParam<T, T> lt0(T number) {
        return lt(number, 0);
    }

    /**
     * 判断参数小于等于指定数值
     *
     * @param number
     * @param other
     * @return
     */
    public static <T extends Number> ImmutableParam<T, T> le(T number, int other) {
        return with(number).when(e -> e.intValue() <= other);
    }

    /**
     * 判断参数小于等于0
     *
     * @param number
     * @return
     */
    public static <T extends Number> ImmutableParam<T, T> le0(T number) {
        return le(number, 0);
    }


    /**
     * 构建参数对象
     *
     * @param parameter
     * @param <T>
     * @return
     */
    public static <T> ImmutableParam<T, T> $(T parameter) {
        return with(parameter);
    }

    /**
     * 构建参数对象
     *
     * @param parameter 原始参数
     * @param <T>
     * @return
     */
    public static <T> ImmutableParam<T, T> with(T parameter) {
        return new ImmutableParam<>(parameter, e -> true, Function.identity());
    }

    /**
     * 构建参数对象,绑定条件断言
     *
     * @param parameter 原始参数
     * @param predicate 条件断言
     * @param <T>
     * @return
     */
    public static <T> ImmutableParam<T, T> with(T parameter, Predicate<T> predicate) {
        return new ImmutableParam<>(parameter, predicate, Function.identity());
    }

    /**
     * 构建参数对象,绑定条件断言和参数转换函数
     *
     * @param parameter 原始参数
     * @param predicate 条件断言
     * @param function  参数转换函数
     * @param <T>
     * @param <P>
     * @return
     */
    public static <T, P> ImmutableParam<P, T> with(P parameter,
                                                   Predicate<P> predicate,
                                                   Function<P, T> function) {
        return new ImmutableParam<>(parameter, predicate, function);
    }


    /**
     * 不变参数类型，通过条件断言和转换函数实现参数的校验和使用
     *
     * @param <OUT>
     * @param <IN>
     */
    public static class ImmutableParam<IN, OUT> implements Param<OUT> {

        /**
         * 初始参数
         */
        private IN parameter;
        /**
         * 条件断言,用于条件判定
         */
        private Predicate<IN> condition;
        /**
         * 转换函数,用于参数处理
         */
        private Function<IN, OUT> resolver;

        /**
         * @param parameter 原始参数
         * @param condition 条件断言
         * @param resolver  转换函数
         */
        public ImmutableParam(IN parameter, Predicate<IN> condition, Function<IN, OUT> resolver) {
            this.parameter = parameter;
            this.condition = condition;
            this.resolver = resolver;
        }

        /**
         * 绑定指定条件
         *
         * @param condition
         * @return
         */
        public ImmutableParam<IN, IN> when(Predicate<IN> condition) {
            return new ImmutableParam<>(parameter, condition, Function.identity());
        }


        /**
         * 将当前条件取反
         *
         * @return
         */
        public ImmutableParam<IN, OUT> negate() {
            return new ImmutableParam<>(parameter, condition.negate(), resolver);
        }

        /**
         * 绑定转换函数
         *
         * @param resolver 对入参进行转换
         * @param <OUT2>
         * @return
         */
        public <OUT2> ImmutableParam<IN, OUT2> then(Function<IN, OUT2> resolver) {
            return new ImmutableParam<>(parameter, condition, resolver);
        }

        /**
         * 绑定转换函数
         *
         * @param resolver 对出参进行转换
         * @param <OUT2>
         * @return
         */
        public <OUT2> ImmutableParam<IN, OUT2> andThen(Function<OUT, OUT2> resolver) {
            return new ImmutableParam<>(parameter, condition, this.resolver.andThen(resolver));
        }

        /**
         * 验证条件
         *
         * @return
         */
        @Override
        public boolean test() {
            return condition.test(parameter);
        }

        /**
         * 返回处理后的参数
         *
         * @return
         */
        @Override
        public OUT get() {
            return resolver.apply(parameter);
        }


    }

}
