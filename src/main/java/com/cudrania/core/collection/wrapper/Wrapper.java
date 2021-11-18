package com.cudrania.core.collection.wrapper;

/**
 * 包装对象
 *
 * @author scorpio
 * @version 1.0.0
 
 */
public interface Wrapper<T> {

    /**
     * 返回原生对象
     *
     * @return
     */
    T unwrap();
}
