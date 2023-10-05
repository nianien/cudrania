package com.cudrania.core.collection;

import com.cudrania.core.arrays.ArrayUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 集合工具类,提供对集合各种扩展操作的支持
 *
 * @author skyfalling
 */
public class CollectionUtils {


    /**
     * 判断集合是否为空
     *
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }


    /**
     * 判断集合是否不为空
     *
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    /**
     * 分批处理{@link Iterable}元素
     *
     * @param iterable 可遍历集合
     * @param limit    批量处理元素数量限制
     * @param consumer 元素处理对象
     */
    public static <E> void doBatch(Iterable<E> iterable, int limit, Consumer<List<E>> consumer) {
        List<E> subList = new ArrayList<>(limit);
        for (E e : iterable) {
            subList.add(e);
            if (subList.size() == limit) {
                consumer.accept(subList);
                subList = new ArrayList<>(limit);
            }
        }
        if (subList.size() > 0) {
            consumer.accept(subList);
        }
    }

    /**
     * 将Iterator对象转化成Enumeration对象
     *
     * @param iterator Iterator对象实例
     * @return Enumeration对象实例
     */
    public static <T> Enumeration<T> enumeration(Iterator<T> iterator) {
        return new Enumeration<T>() {

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public T nextElement() {
                return iterator.next();
            }

        };

    }

    /**
     * 将Enumeration对象转化成Iterator对象
     *
     * @param enumeration Enumeration对象实例
     * @return Iterator对象实例
     */
    public static <T> List<T> list(Enumeration<T> enumeration) {
        List<T> list = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            list.add(enumeration.nextElement());
        }
        return list;
    }

    /**
     * 数组转链表
     *
     * @param array 数组对象
     * @return List泛型实例
     */
    public static <T> List<T> list(T[] array) {
        return Arrays.stream(array).collect(Collectors.toList());
    }

    /**
     * 可枚举对象转链表
     *
     * @param iterable 可遍历集合
     * @return List泛型实例
     */
    public static <T> List<T> list(Iterable<T> iterable) {
        return list(iterable, Function.identity());
    }


    /**
     * 取元素的某个属性形成新的链表
     *
     * @param iterable 可遍历集合
     * @param func     生成元素的函数
     * @param <T>      属性类型的泛型约束
     * @return 属性列表
     */
    public static <T, V> List<T> list(Iterable<V> iterable, Function<V, T> func) {
        List<T> list;
        if (iterable instanceof Collection) {
            list = new ArrayList<>(((Collection) iterable).size());
        } else {
            list = new ArrayList<>();
        }
        for (V o : iterable) {
            list.add(func.apply(o));
        }
        return list;
    }

    /**
     * 枚举器转链表
     *
     * @param iterator 枚举器
     * @param <T>
     * @return
     */
    public static <T> List<T> list(Iterator<T> iterator) {
        List<T> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }


    /**
     * 删除链表从from到to(不包括)索引位置的元素
     *
     * @param list
     * @param from
     * @param to
     * @param <T>
     */
    public static <T> void remove(List<T> list, int from, int to) {
        list.subList(from, to).clear();
    }

    /**
     * 删除集合中满足条件的元素
     *
     * @param list
     * @param predicate
     * @param <T>
     */
    public static <T> void remove(Collection<T> list, Predicate<T> predicate) {
        list.removeIf(predicate);
    }

    /**
     * 删除集合中null元素
     *
     * @param list
     * @param <T>
     */
    public static <T> void removeNull(Collection<T> list) {
        list.removeIf(Objects::isNull);
    }

    /**
     * 删除Map中value为null的键值对
     *
     * @param map
     * @param <K>
     * @param <V>
     */
    public static <K, V> void removeNull(Map<K, V> map) {
        map.values().removeIf(Objects::isNull);
    }

    /**
     * 集合转Map,keyGen函数执行结果作为key值,元素本身作为value值
     *
     * @param iterable 可遍历集合
     * @param keyGen   生成key的函数
     * @param <K>      key的泛型约束
     * @param <V>      value的泛型约束
     */
    public static <K, V> Map<K, V> map(Iterable<V> iterable, Function<V, K> keyGen) {
        return map(iterable, keyGen, Function.identity());
    }

    /**
     * 集合转Map,keyGen函数执行结果作为key值,valueGen执行结果作为value值
     *
     * @param iterable 可遍历集合
     * @param keyGen   生成Key的函数
     * @param valueGen 作为Value的函数
     * @param <K>      key的泛型约束
     * @param <V>      value的泛型约束
     * @param <T>      元素的泛型约束
     */
    public static <K, V, T> Map<K, V> map(Iterable<T> iterable, Function<T, K> keyGen, Function<T, V> valueGen) {
        Map<K, V> map;
        if (iterable instanceof Collection) {
            map = new HashMap<>(((Collection) iterable).size());
        } else {
            map = new HashMap<>();
        }
        for (T obj : iterable) {
            map.put(keyGen.apply(obj), valueGen.apply(obj));
        }
        return map;
    }


    /**
     * 将集合中元素按照指定函数分组
     *
     * @param iterable 可遍历集合
     * @param keyGen   生成key的函数
     * @param <K>      key的泛型约束
     * @param <V>      value的泛型约束
     */
    public static <K, V> Map<K, List<V>> groupBy(Iterable<V> iterable, Function<V, K> keyGen) {
        return groupBy(iterable, keyGen, Function.identity());
    }

    /**
     * 将集合中元素按照指定函数分组
     *
     * @param iterable 可遍历集合
     * @param keyGen   生成key的函数
     * @param <K>      key的泛型约束
     * @param <V>      value的泛型约束
     */
    public static <K, V, T> Map<K, List<V>> groupBy(Iterable<T> iterable, Function<T, K> keyGen, Function<T, V> valueGen) {
        Map<K, List<V>> map;
        if (iterable instanceof Collection) {
            map = new HashMap<>(((Collection) iterable).size());
        } else {
            map = new HashMap<>();
        }
        for (T obj : iterable) {
            K keyObj = keyGen.apply(obj);
            V valueObj = valueGen.apply(obj);
            map.computeIfAbsent(keyObj, (K k) -> new ArrayList<>()).add(valueObj);
        }
        return map;
    }


    /**
     * 将集合中元素按照数量分组
     *
     * @param iterable 可遍历集合
     * @param limit    每组元素个数
     */
    public static <T> List<List<T>> grouped(Iterable<T> iterable, int limit) {
        List<List<T>> res = new ArrayList<>();
        doBatch(iterable, limit, l -> res.add(l));
        return res;
    }


    /**
     * 数组对象转列表
     *
     * @param source
     * @return
     */
    public static <T> List<T> arrayToList(Object source) {
        return (List<T>) Arrays.asList(ArrayUtils.toObjectArray(source));
    }


}
