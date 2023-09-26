package com.cudrania.core.collection.wrapper;

import com.cudrania.core.collection.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 定义绑定到{@link Iterable<E>}对象的函数集合
 *
 * @param <E>
 */
public interface IterSupplier<E> extends Supplier<Iterable<E>> {


    /**
     * 分批处理{@link Iterable}元素
     *
     * @param limit    批量处理元素数量限制
     * @param consumer 元素处理对象
     */
    default void doBatch(int limit, Consumer<List<E>> consumer) {
        CollectionUtils.doBatch(get(), limit, consumer);
    }

    /**
     * 集合转Map,keyGen函数执行结果作为key值,元素本身作为value值
     *
     * @param keyGen 生成key的函数
     * @param <K>    key的泛型约束
     */
    default <K> Map<K, E> map(Function<E, K> keyGen) {
        return map(keyGen, Function.identity());
    }

    /**
     * 集合转Map,keyGen函数执行结果作为key值,valueGen执行结果作为value值
     *
     * @param keyGen   生成Key的函数
     * @param valueGen 作为Value的函数
     * @param <K>      key的泛型约束
     * @param <V>      value的泛型约束
     */
    default <K, V> Map<K, V> map(Function<E, K> keyGen, Function<E, V> valueGen) {
        return CollectionUtils.map(get(), keyGen, valueGen);
    }


    /**
     * 将集合中元素按照指定函数分组
     *
     * @param keyGen 生成key的函数
     * @param <K>    key的泛型约束
     * @param <V>    value的泛型约束
     */
    default <K, V> Map<K, List<V>> groupBy(Function<E, K> keyGen, Function<E, V> valueGen) {
        return CollectionUtils.groupBy(get(), keyGen, valueGen);
    }

    /**
     * 将集合中元素按照指定函数分组
     *
     * @param keyGen 生成key的函数
     * @param <K>    key的泛型约束
     */
    default <K> Map<K, List<E>> groupBy(Function<E, K> keyGen) {
        return groupBy(keyGen, Function.identity());
    }

    /**
     * 将集合中元素按照数量分组
     *
     * @param size 每组元素个数
     */
    default List<List<E>> grouped(int size) {
        return CollectionUtils.grouped(get(), size);
    }
}
