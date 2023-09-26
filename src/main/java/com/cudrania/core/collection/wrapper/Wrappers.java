package com.cudrania.core.collection.wrapper;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Supplier;

/**
 * 提供{@link List}/{@link Set}/{@link Map}增强对象的工具类
 *
 * @author scorpio
 * @version 1.0.0
 */
public class Wrappers {

    /**
     * 代理指定{@link Map}实例
     *
     * @param target
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> MapWrapper<K, V> map(Map<K, V> target) {
        return (MapWrapper) proxy(target, MapWrapper.class);
    }


    /**
     * 代理默认{@link HashMap}实例，并初始化键值对
     *
     * @param key
     * @param value
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> MapWrapper<K, V> map(K key, V value) {
        return Wrappers.<K, V>map().$put(key, value);
    }

    /**
     * 代理默认{@link HashMap}实例
     *
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> MapWrapper<K, V> map() {
        return map(new HashMap<>());
    }


    /**
     * 代理指定{@link List}实例
     *
     * @param target
     * @param <E>
     * @return
     */
    public static <E> ListWrapper<E> list(List<E> target) {
        return (ListWrapper) proxy(target, ListWrapper.class);
    }


    /**
     * 代理默认{@link ArrayList}实例
     *
     * @param <E>
     * @return
     */
    public static <E> ListWrapper<E> list() {
        return list(new ArrayList<>());
    }

    /**
     * 代理{@link ArrayList}实例，并初始化元素
     *
     * @param <E>
     * @return
     */
    public static <E> ListWrapper<E> list(E value) {
        return Wrappers.<E>list().$add(value);
    }


    /**
     * 代理指定{@link Set}实例
     *
     * @param target
     * @param <E>
     * @return
     */
    public static <E> SetWrapper<E> set(Set<E> target) {
        return (SetWrapper) proxy(target, SetWrapper.class);
    }


    /**
     * 代理默认{@link HashSet}实例
     *
     * @param <E>
     * @return
     */
    public static <E> SetWrapper<E> set() {
        return set(new HashSet<>());
    }

    /**
     * 代理{@link HashSet}实例，并初始化元素
     *
     * @param <E>
     * @return
     */
    public static <E> SetWrapper<E> set(E value) {
        return Wrappers.<E>set().$add(value);
    }

    /**
     * 代理指定类型对象
     *
     * @param target
     * @param clazz
     * @return
     */
    private static Object proxy(final Object target, Class clazz) {
        return Proxy.newProxyInstance(Wrappers.class.getClassLoader(),
                new Class[]{clazz}, (proxy, method, args) -> {
                    Delegate delegate = method.getDeclaredAnnotation(Delegate.class);
                    if (delegate != null) {
                        String name = delegate.value();
                        clazz.getMethod(name, method.getParameterTypes()).invoke(target, args);
                        return proxy;
                    }
                    if (Supplier.class.isAssignableFrom(method.getDeclaringClass())) {
                        Object newTarget;
                        if (method.getDeclaringClass() == IterSupplier.class) {
                            newTarget = (IterSupplier) () -> (Iterable) target;
                        } else if (method.getDeclaringClass() == MapSupplier.class) {
                            newTarget = (MapSupplier) () -> (Map) target;
                        } else {
                            newTarget = (Supplier) () -> target;
                        }
                        return method.invoke(newTarget, args);
                    }
                    return method.invoke(target, args);
                });
    }

}
