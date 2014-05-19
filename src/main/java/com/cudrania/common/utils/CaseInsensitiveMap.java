package com.cudrania.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 当键值类型为String类型时,不区分大小写,但仍保持原有键值不变<br>
 *
 * @param <K>
 * @param <V>
 * @author skyfalling
 */
public class CaseInsensitiveMap<K, V> extends HashMap<K, V> {
    private static final long serialVersionUID = 1L;
    private Map<String, String> keyMap = new HashMap<String, String>();

    /**
     */
    public CaseInsensitiveMap() {
    }

    /**
     * @param initialCapacity
     */
    public CaseInsensitiveMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * @param initialCapacity
     * @param loadFactor
     */
    public CaseInsensitiveMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * @param map
     */
    public CaseInsensitiveMap(Map<K, V> map) {
        super(Math.max((int) (map.size() / 0.75f) + 1,
                16), 0.75f);
        for (Entry<K, V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 获取Key值所映射的Value值<br>
     * 当Key为String类型时不分大小写
     *
     * @see java.util.HashMap#get(Object)
     */
    @Override
    public V get(Object key) {
        return super.get(findRealKey(key));
    }

    /**
     * 设置Key键所映射的Value值<br>
     * 当Key为String类型且存在相等(不分大小)的Key值时,使用原来的Key值
     *
     * @see java.util.HashMap#put(Object, Object)
     */
    @Override
    public V put(K key, V value) {
        if (key instanceof String) {
            String keyStr = key.toString();
            String oldKey = keyMap.put(keyStr.toLowerCase(), keyStr);
            if (oldKey != null) {
                super.remove(oldKey);
            }
        }
        return super.put((K) findRealKey(key), value);
    }

    /**
     * 判断是否包含指定的Key值<br>
     * 当Key为String类型时不分大小写
     *
     * @see java.util.HashMap#containsKey(Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(findRealKey(key));
    }

    /**
     * 删除Key值映射的数据<br>
     * 当Key为String类型时不分大小写
     *
     * @see java.util.HashMap#remove(Object)
     */
    @Override
    public V remove(Object key) {
        if (key instanceof String) {
            keyMap.remove(key.toString().toLowerCase());
        }
        return super.remove(findRealKey(key));
    }

    @Override
    public void clear() {
        super.clear();
        keyMap.clear();
    }

    /**
     * 根据给定的Key查找Map中存在的Key值
     *
     * @param key
     * @return
     */
    private Object findRealKey(Object key) {
        if (key instanceof String) {
            String realKey = keyMap.get(key.toString().toLowerCase());
            if (realKey != null) return realKey;
        }
        return key;
    }

}
