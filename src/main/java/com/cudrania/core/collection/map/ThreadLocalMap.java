package com.cudrania.core.collection.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 存储本地线程变量的Map类
 *
 * @param <K>
 * @param <V>
 * @author skyfalling
 */
public class ThreadLocalMap<K, V> extends ThreadLocal<Map<K, V>> implements Map<K, V> {

    @Override
    protected Map<K, V> initialValue() {
        return new HashMap<K, V>();
    }

    @Override
    public Map<K, V> get() {
        return super.get();
    }

    @Override
    public void set(Map<K, V> value) {
        super.set(value);
    }

    @Override
    public int size() {
        return this.get().size();
    }

    @Override
    public boolean isEmpty() {

        return this.get().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {

        return this.get().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {

        return this.get().containsValue(value);
    }

    @Override
    public V get(Object key) {

        return this.get().get(key);
    }

    @Override
    public V put(K key, V value) {

        return this.get().put(key, value);
    }

    @Override
    public V remove(Object key) {
        return this.get().remove(key);
    }

    @Override
    public void clear() {
        this.get().clear();

    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.get().putAll(m);

    }

    @Override
    public Set<K> keySet() {
        return this.get().keySet();
    }

    @Override
    public Collection<V> values() {
        return this.get().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.get().entrySet();
    }

    @Override
    public int hashCode() {
        return this.get().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.get().equals(obj);
    }

    @Override
    public String toString() {
        return get().toString();
    }
}
