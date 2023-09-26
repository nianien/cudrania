package com.cudrania.core.collection.wrapper;

import java.util.List;

/**
 * 继承自{@link List}的增强类，支持链式调用
 *
 * @param <E>
 */
public interface ListWrapper<E> extends List<E>, IterSupplier<E> {

    /**
     * 代理{@link List#add(Object)}方法
     *
     * @param value
     * @return 返回当前对象
     */
    @Delegate("add")
    ListWrapper<E> $add(E value);

    /**
     * 代理{@link List#add(int, Object)}方法
     *
     * @param value
     * @return 返回当前对象
     */
    @Delegate("add")
    ListWrapper<E> $add(int index, E value);

    /**
     * 代理{@link List#remove(Object)}方法
     *
     * @param key
     * @return 返回当前对象
     */
    @Delegate("remove")
    ListWrapper<E> $remove(E key);


    /**
     * 代理{@link List#remove(int)}方法
     *
     * @param index
     * @return 返回当前对象
     */
    @Delegate("remove")
    ListWrapper<E> $remove(int index);


    /**
     * 代理{@link List#clear()}
     *
     * @return
     */
    @Delegate("clear")
    ListWrapper<E> $clear();

    @Delegate("this")
    List<E> get();
}