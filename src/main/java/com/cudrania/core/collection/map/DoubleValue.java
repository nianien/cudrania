package com.cudrania.core.collection.map;

/**
 * 可以将两种数据类型绑定,用作DoubleMap对象Value值的数据类型
 *
 * @param <V1>
 * @param <V2>
 * @author skyfalling
 */
public class DoubleValue<V1, V2> {

    private V1 value1;
    private V2 value2;

    public DoubleValue(V1 value1, V2 value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public V1 getValue1() {
        return value1;
    }

    public V2 getValue2() {
        return value2;
    }

    public void setValue1(V1 value1) {
        this.value1 = value1;

    }

    public void setValue2(V2 value2) {
        this.value2 = value2;
    }
}
