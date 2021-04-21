package com.cudrania.core.collection.map;

import java.util.HashMap;

/**
 * 具有统计功能的Map实现类<br>
 *
 * @param <T>
 * @author skyfalling
 */
public class CounterMap<T> extends HashMap<T, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * 将key对应的计数加n,如果加n后小于0则删除该key并返回0,否则返回对应的计数值
     *
     * @param key
     * @return
     */
    public int add(T key, int n) {
        Integer value = get(key);
        value += n;
        if (value > 0) {
            put(key, value);
            return value;
        } else {
            remove(key);
        }
        return 0;
    }

    /**
     * 将key对应的计数加1,返回加1后的计数
     *
     * @param key
     * @return
     */
    public int increase(T key) {
        return add(key, 1);
    }

    /**
     * 将key对应的计数减1,返回减1后的计数
     *
     * @param key
     * @return
     */
    public int decrease(T key) {
        return add(key, -1);
    }

    /**
     * 获取key对应的计数,如果不存在,则返回0
     */
    @Override
    public Integer get(Object key) {
        Integer n = super.get(key);
        return n == null ? 0 : n;
    }

}
