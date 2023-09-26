package com.cudrania.core.collection.wrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link Set}接口的包装类,包装Set实例以支持链式语法<p>
 * 如果未提供Set实例,则默认为{@link  HashSet}实现<p>
 *
 * @author skyfalling
 */
public interface SetWrapper<E> extends Set<E>, CollectionWrapper<E> {


    /**
     * 代理{@link Set#add(Object)}方法
     *
     * @param value
     * @return 返回当前对象
     */
    SetWrapper<E> $add(E value);

    /**
     * 代理{@link Set#remove(Object)}方法
     *
     * @param key
     * @return 返回当前对象
     */
    SetWrapper<E> $remove(E key);


    /**
     * 代理{@link Set#clear()}
     *
     * @return
     */
    SetWrapper<E> $clear();

    /**
     * 返回原生对象
     *
     * @return
     */
    Set<E> $this();
}
