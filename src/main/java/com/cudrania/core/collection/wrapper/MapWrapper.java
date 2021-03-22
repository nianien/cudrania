package com.cudrania.core.collection.wrapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * {@link Map}接口的包装类,包装Map实例以支持链式语法<br/>
 * 如果未提供Map实例,则默认为{@link HashMap}实现<br/>
 *
 * @author skyfalling
 */
public class MapWrapper<K, V> implements Map<K, V>, Wrapper<Map<K, V>> {

    private Map<K, V> map;

    /**
     * 构造方法,默认Map实例
     */
    public MapWrapper() {
        this(new HashMap<>());
    }

    /**
     * 构造方法,指定Map实例
     *
     * @param map
     */
    public MapWrapper(Map<K, V> map) {
        this.map = map;
    }

    /**
     * 构造方法,默认Map实例并提供初始键值对
     *
     * @param k
     * @param v
     */
    public MapWrapper(K k, V v) {
        this(new HashMap<>(), k, v);
    }

    /**
     * 构造方法,指定Map实例以及初始键值对
     *
     * @param map
     * @param key
     * @param value
     */
    protected MapWrapper(Map<K, V> map, K key, V value) {
        this.map = map;
        this.put(key, value);
    }

    /**
     * 添加键值对
     *
     * @param key
     * @param value
     * @return 返回当前对象
     * @see Map#put(K, V)
     */
    public MapWrapper<K, V> with(K key, V value) {
        map.put(key, value);
        return this;
    }

    /**
     * 添加键值对
     *
     * @param map
     * @return 返回当前对象
     * @see Map#putAll(Map)
     */
    public MapWrapper<K, V> with(Map<? extends K, ? extends V> map) {
        this.putAll(map);
        return this;
    }

    /**
     * 删除键值
     *
     * @param keys
     * @return 返回当前对象
     * @see Map#remove(Object)
     */
    public MapWrapper<K, V> without(Collection keys) {
        for (Object key : keys) {
            this.remove(key);
        }
        return this;
    }

    /**
     * 删除键值
     *
     * @param keys
     * @return 返回当前对象
     * @see Map#remove(Object)
     */
    public MapWrapper<K, V> without(Object... keys) {
        for (Object key : keys) {
            this.remove(key);
        }
        return this;
    }


    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        map.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        map.replaceAll(function);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return map.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return map.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return map.merge(key, value, remappingFunction);
    }

    @Override
    public Map<K, V> unwrap() {
        return map;
    }
}