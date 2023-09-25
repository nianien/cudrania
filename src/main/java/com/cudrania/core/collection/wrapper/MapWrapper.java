package com.cudrania.core.collection.wrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link Map}接口的包装类,包装Map实例以支持链式语法<p>
 * 如果未提供Map实例,则默认为{@link HashMap}实现<p>
 *
 * @author skyfalling
 */
public interface MapWrapper<K, V> extends Map<K, V> {


    /**
     * 代理{@link Map#put(Object, Object)}方法
     *
     * @param key
     * @param value
     * @return 返回当前对象
     */
    MapWrapper<K, V> $put(K key, V value);

    /**
     * 代理{@link Map#remove(Object)}方法
     *
     * @param key
     * @return 返回当前对象
     */
    MapWrapper<K, V> $remove(K key);


    /**
     * 代理{@link Map#remove(Object, Object)}方法
     *
     * @param key
     * @return 返回当前对象
     */
    MapWrapper<K, V> $remove(K key, V value);


    /**
     * 代理{@link Map#clear()}
     *
     * @return
     */
    MapWrapper<K, V> $clear();

    /**
     * 返回原生对象
     *
     * @return
     */
    Map<K, V> $this();

}