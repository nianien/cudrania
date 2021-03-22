package com.cudrania.core.collection.map;

import java.util.HashMap;
import java.util.Map;

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
     * 构造方法,指定初始容量
     *
     * @param initialCapacity 初始容量
     */
    public CaseInsensitiveMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * 构造方法,指定初始容量和负载因子
     *
     * @param initialCapacity 初始容量
     * @param loadFactor      负载因子
     */
    public CaseInsensitiveMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * 构造方法
     *
     * @param map 初始数据拷贝对象,将其映射数据按照默认遍历顺序拷贝到当前对象中<br/>
     *            需要注意的是:如果Map的key有重复（不区分大小写）,无法保证覆盖顺序.此时建议使用{@link java.util.LinkedHashMap}或{@link java.util.TreeMap}以保证覆盖顺序.
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
