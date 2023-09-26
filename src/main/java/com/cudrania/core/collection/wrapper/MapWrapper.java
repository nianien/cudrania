package com.cudrania.core.collection.wrapper;

import java.util.Map;

/**
 * 继承自{@link Map}的增强类，支持链式调用
 *
 * @author skyfalling
 */
public interface MapWrapper<K, V> extends Map<K, V>, MapSupplier<K, V> {


    /**
     * 代理{@link Map#put(Object, Object)}方法
     *
     * @param key
     * @param value
     * @return 返回当前对象
     */
    @Delegate("put")
    MapWrapper<K, V> $put(K key, V value);

    /**
     * 代理{@link Map#remove(Object)}方法
     *
     * @param key
     * @return 返回当前对象
     */
    @Delegate("remove")
    MapWrapper<K, V> $remove(K key);


    /**
     * 代理{@link Map#remove(Object, Object)}方法
     *
     * @param key
     * @return 返回当前对象
     */
    @Delegate("remove")
    MapWrapper<K, V> $remove(K key, V value);


    /**
     * 代理{@link Map#clear()}
     *
     * @return
     */
    @Delegate("clear")
    MapWrapper<K, V> $clear();


}