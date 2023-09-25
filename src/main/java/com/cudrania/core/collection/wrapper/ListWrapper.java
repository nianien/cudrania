package com.cudrania.core.collection.wrapper;

import java.util.*;

/**
 * {@link List}接口的包装类,包装List实例以支持链式语法<p>
 * 如果未提供List实例,则默认为{@link ArrayList}实现<p>
 *
 * @author skyfalling
 */
public interface ListWrapper<E> extends List<E> {

    /**
     * 代理{@link List#add(Object)}方法
     *
     * @param value
     * @return 返回当前对象
     */
    ListWrapper<E> $add(E value);

    /**
     * 代理{@link List#add(int, Object)}方法
     *
     * @param value
     * @return 返回当前对象
     */
    ListWrapper<E> $add(int index, E value);

    /**
     * 代理{@link List#remove(Object)}方法
     *
     * @param key
     * @return 返回当前对象
     */
    ListWrapper<E> $remove(E key);


    /**
     * 代理{@link List#remove(int)}方法
     *
     * @param index
     * @return 返回当前对象
     */
    ListWrapper<E> $remove(int index);


    /**
     * 代理{@link List#clear()}
     *
     * @return
     */
    ListWrapper<E> $clear();

    /**
     * 返回原生对象
     *
     * @return
     */
    List<E> $this();
}
