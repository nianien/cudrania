package com.cudrania.core.collection.map;

import java.util.HashMap;


/**
 * 双值Map对象
 *
 * @param <K>
 * @param <V1>
 * @param <V2>
 * @author skyfalling
 */
public class DuoMap<K, V1, V2> extends HashMap<K, DuoValue<V1, V2>> {

    private static final long serialVersionUID = 1L;


    /**
     * 获取key对应的第一个数据对象
     *
     * @param key
     * @return
     */
    public V1 getValue1(Object key) {
        return this.get(key).getValue1();

    }

    /**
     * 获取key对应的第二个数据对象
     *
     * @param key
     * @return
     */
    public V2 getValue2(Object key) {
        return this.get(key).getValue2();

    }

    /**
     * 添加一个键值对<br>
     * 这里将创建一个DoubleValue(value1,value2)作为value值
     *
     * @param key
     * @param value1
     * @param value2
     * @return
     */
    public DuoValue<V1, V2> put(K key, V1 value1, V2 value2) {
        return this.put(key, new DuoValue<>(value1, value2));
    }

    /**
     * 更新key对应的第一个数据对象,返回更新前的值
     *
     * @param key
     * @param value1
     * @return
     */
    public V1 updateValue1(K key, V1 value1) {
        DuoValue<V1, V2> duoValue = this.computeIfAbsent(key, (k) -> new DuoValue<>(value1, null));
        if (duoValue == null) {
            return null;
        }
        V1 old = duoValue.getValue1();
        duoValue.setValue1(value1);
        return old;
    }


    /**
     * 更新key对应的第二个数据对象,返回更新前的值
     *
     * @param key
     * @param value2
     * @return
     */
    public V2 updateValue2(K key, V2 value2) {
        DuoValue<V1, V2> duoValue = this.computeIfAbsent(key, (k) -> new DuoValue<>(null, value2));
        if (duoValue == null) {
            return null;
        }
        V2 old = duoValue.getValue2();
        duoValue.setValue2(value2);
        return old;
    }
}
