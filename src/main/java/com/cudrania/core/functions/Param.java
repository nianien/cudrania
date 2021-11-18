package com.cudrania.core.functions;

/**
 * 条件参数，当{@link #test()}为真时，可获取参数对象{@link #get()}
 *
 * @author scorpio
 * @version 1.0.0
 
 */
public interface Param<T> {

    /**
     * 是否满足条件
     *
     * @return
     */
    boolean test();

    /**
     * 获取参数值
     *
     * @return
     */
    T get();

    /**
     * 如果{@link #test()}为真,获取参数值,否则返回defaultValue
     *
     * @param defaultValue
     * @return
     */
    default T compute(T defaultValue) {
        return test() ? get() : defaultValue;
    }
}
