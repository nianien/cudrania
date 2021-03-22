package com.cudrania.core.collection.map;

import java.util.Map.Entry;

/**
 * Key/Value键值对, <code>{@link java.util.Map.Entry }</code>的接口实现
 *
 * @param <K>
 * @param <V>
 * @author skyfalling
 */
public class Pair<K, V> implements Entry<K, V> {

    private K key;
    private V value;

    /**
     * @see java.util.Map.Entry#getKey()
     */
    @Override
    public K getKey() {
        return key;
    }

    /**
     * @see java.util.Map.Entry#getValue()
     */
    @Override
    public V getValue() {
        return value;
    }

    /**
     * @see java.util.Map.Entry#setValue(Object)
     */
    @Override
    public V setValue(V value) {
        V old = value;
        this.value = value;
        return old;
    }

    /**
     * 构造方法
     *
     * @param key
     * @param value
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return "{" + key + ":" + value + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (!key.equals(pair.key)) return false;
        if (!value.equals(pair.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
