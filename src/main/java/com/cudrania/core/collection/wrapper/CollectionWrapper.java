package com.cudrania.core.collection.wrapper;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 集合包装类
 *
 * @param <E>
 */
public interface CollectionWrapper<E> {


    default void doBatch(int limit, Consumer<List<E>> consumer) {
        List<E> subList = new ArrayList<>(limit);
        for (Object e : $this()) {
            subList.add((E) e);
            if (subList.size() == limit) {
                consumer.accept(subList);
                subList = new ArrayList<>(limit);
            }
        }
        if (subList.size() > 0) {
            consumer.accept(subList);
        }
    }

    /**
     * 集合转Map,属性keyProperty作为Map的key值,元素本身作为Map的value值
     *
     * @param keyGen 生成key的函数
     * @param <K>    key的泛型约束
     */
    default <K> Map<K, E> map(Function<E, K> keyGen) {
        return map(keyGen, Function.identity());
    }

    /**
     * 集合转Map,属性keyProperty作为Map的key值,属性valueProperty作为Map的value值
     *
     * @param keyGen   生成Key的函数
     * @param valueGen 作为Value的函数
     * @param <K>      key的泛型约束
     * @param <V>      value的泛型约束
     */
    default <K, V> Map<K, V> map(Function<E, K> keyGen, Function<E, V> valueGen) {
        Collection<E> collection = $this();
        Map<K, V> map = new HashMap<>(collection.size());
        for (E obj : collection) {
            map.put(keyGen.apply(obj), valueGen.apply(obj));
        }
        return map;
    }

    Collection<E> $this();

}
