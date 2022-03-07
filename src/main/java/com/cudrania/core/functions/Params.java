package com.cudrania.core.functions;


import com.cudrania.core.arrays.ArrayUtils;
import com.cudrania.core.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
    public static <T> ImmutableParam<T> notNull(T obj) {
        return with(obj).when(Objects::nonNull);
    }

    /**
     * 判断字符串不为空
     *
     * @param str
     * @return
     */
    public static ImmutableParam<String> notEmpty(String str) {
        return with(str, StringUtils::isNotEmpty);
    }


    /**
     * 判断集合不为空
     *
     * @param collection
     * @return
     */
    public static <T extends Collection<E>, E> ImmutableParam<T> notEmpty(T collection) {
        return with(collection, CollectionUtils::isNotEmpty);
    }


    /**
     * 判断Map不为空
     *
     * @param map
     * @return
     */
    public static <T extends Map<?, ?>> ImmutableParam<T> notEmpty(T map) {
        return with(map, m -> m != null && !m.isEmpty());
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
    public static <T extends Number> ImmutableParam<T> eq(T number, int other) {
        return with(number).when(e -> e.intValue() == other);
    }


    /**
     * 判断等于0
     *
     * @param number
     * @return
     */
    public static <T extends Number> ImmutableParam<T> eq0(T number) {
        return eq(number, 0);
    }

    /**
     * 判断不等于指定数值
     *
     * @param number
     * @param other
     * @return
     */
    public static <T extends Number> ImmutableParam<T> ne(T number, int other) {
        return with(number).when(e -> e.intValue() != other);
    }


    /**
     * 判断不等于0
     *
     * @param number
     * @return
     */
    public static <T extends Number> ImmutableParam<T> ne0(T number) {
        return ne(number, 0);
    }

    /**
     * 判断参数大于指定数值
     *
     * @param number
     * @param other
     * @return
     */
    public static <T extends Number> ImmutableParam<T> gt(T number, int other) {
        return with(number).when(e -> e.intValue() > other);
    }


    /**
     * 判断参数大于0
     *
     * @param number
     * @return
     */
    public static <T extends Number> ImmutableParam<T> gt0(T number) {
        return gt(number, 0);
    }

    /**
     * 判断参数大于等于指定数值
     *
     * @param number
     * @param other
     * @return
     */
    public static <T extends Number> ImmutableParam<T> ge(T number, int other) {
        return with(number).when(e -> e.intValue() >= other);
    }

    /**
     * 判断参数大于等于指定数值
     *
     * @param number
     * @return
     */
    public static <T extends Number> ImmutableParam<T> ge0(T number) {
        return ge(number, 0);
    }

    /**
     * 判断参数小于指定数值
     *
     * @param number
     * @param other
     * @return
     */
    public static <T extends Number> ImmutableParam<T> lt(T number, int other) {
        return with(number).when(e -> e.intValue() < other);
    }

    /**
     * 判断参数小于0
     *
     * @param number
     * @return
     */
    public static <T extends Number> ImmutableParam<T> lt0(T number) {
        return lt(number, 0);
    }

    /**
     * 判断参数小于等于指定数值
     *
     * @param number
     * @param other
     * @return
     */
    public static <T extends Number> ImmutableParam<T> le(T number, int other) {
        return with(number).when(e -> e.intValue() <= other);
    }

    /**
     * 判断参数小于等于0
     *
     * @param number
     * @return
     */
    public static <T extends Number> ImmutableParam<T> le0(T number) {
        return le(number, 0);
    }


    /**
     * 构建参数对象
     *
     * @param parameter 原始参数
     * @param <T>
     * @return
     */
    public static <T> ImmutableParam<T> with(T parameter) {
        return with(parameter, x -> true);
    }


    /**
     * 构建参数对象,绑定条件断言和参数转换函数
     *
     * @param parameter 原始参数
     * @param predicate 条件断言
     * @param <T>
     * @return
     */
    public static <T> ImmutableParam<T> with(T parameter,
                                             Predicate<T> predicate) {
        return new ImmutableParam<>(() -> parameter, predicate);
    }


    /**
     * 不变参数类型，通过条件断言和转换函数实现参数的校验和使用
     *
     * @param <IN>
     */
    public static class ImmutableParam<IN> implements Param<IN> {
        /**
         * 初始参数
         */
        private Supplier<IN> provider;
        /**
         * 条件断言,用于条件判定
         */
        private Predicate<IN> condition;

        /**
         * @param provider  原始输入
         * @param condition 条件断言
         */
        private ImmutableParam(Supplier<IN> provider, Predicate<IN> condition) {
            this.provider = provider;
            this.condition = condition;
        }


        /**
         * 追加判定条件
         *
         * @param predicate
         * @return
         */
        public ImmutableParam<IN> when(Predicate<IN> predicate) {
            return new ImmutableParam<>(provider, condition.and(predicate));
        }


        /**
         * 当前条件取反
         *
         * @return
         */
        public ImmutableParam<IN> negate() {
            return new ImmutableParam<>(provider, condition.negate());
        }

        /**
         * 结果转换
         *
         * @param resolver 对输入数据进行转换
         * @param <OUT>
         * @return
         */
        public <OUT> ImmutableParam<OUT> then(Function<IN, OUT> resolver) {
            return new ImmutableParam<>(() -> this.get().map(resolver).orElse(null), x -> true);
        }


        /**
         * 返回处理后的参数
         *
         * @return
         */
        @Override
        public Optional<IN> get() {
            IN in = provider.get();
            if (in != null && condition.test(in)) {
                return Optional.ofNullable(in);
            }
            return Optional.empty();
        }
    }
}
