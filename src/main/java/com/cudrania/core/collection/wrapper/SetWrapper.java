package com.cudrania.core.collection.wrapper;

import java.util.Set;

/**
 * 继承自{@link Set}的增强类，支持链式调用
 *
 * @author skyfalling
 */
public interface SetWrapper<E> extends Set<E>, IterSupplier<E, Set<E>> {


    /**
     * 代理{@link Set#add(Object)}方法
     *
     * @param value
     * @return 返回当前对象
     */
    @Delegate("add")
    SetWrapper<E> $add(E value);

    /**
     * 代理{@link Set#remove(Object)}方法
     *
     * @param key
     * @return 返回当前对象
     */
    @Delegate("remove")
    SetWrapper<E> $remove(E key);


    /**
     * 代理{@link Set#clear()}
     *
     * @return
     */
    @Delegate("clear")
    SetWrapper<E> $clear();


}
