package com.cudrania.core.collection.wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * {@link List}接口的包装类,包装List实例以支持链式语法<br/>
 * 如果未提供List实例,则默认为{@link ArrayList}实现<br/>
 *
 * @author skyfalling
 */
public class ListWrapper<E> implements List<E>, Wrapper<List<E>> {

    private List<E> list;

    /**
     * 构造方法,指定List实例
     *
     * @param elements
     */
    public ListWrapper(E... elements) {
        this(new ArrayList<>(), elements);
    }

    /**
     * 构造方法,默认List实例并提供初始元素
     *
     * @param elements
     */
    public ListWrapper(List<E> list, E... elements) {
        this.list = list;
        this.with(elements);
    }


    /**
     * 添加元素
     *
     * @param elements
     * @return
     * @see List#addAll(java.util.Collection)}
     */
    public ListWrapper<E> with(E... elements) {
        list.addAll(Arrays.asList(elements));
        return this;
    }

    /**
     * 添加元素
     *
     * @param elements
     * @return
     * @see List#addAll(java.util.Collection)}
     */
    public ListWrapper<E> with(Collection<? extends E> elements) {
        list.addAll(elements);
        return this;
    }

    /**
     * 在指定索引位置添加元素
     *
     * @param elements
     * @return
     * @see List#addAll(int, Collection)
     */
    public ListWrapper<E> with(int index, E... elements) {
        list.addAll(index, Arrays.asList(elements));
        return this;
    }

    /**
     * 在指定索引位置添加元素
     *
     * @param elements
     * @return
     * @see List#addAll(int, Collection)
     */
    public ListWrapper<E> with(int index, Collection<? extends E> elements) {
        list.addAll(index, elements);
        return this;
    }


    /**
     * 删除指定索引位置元素
     *
     * @param index
     * @return 返回当前对象
     * @see List#remove(int)
     */
    public ListWrapper<E> without(int index) {
        list.remove(index);
        return this;
    }

    /**
     * 删除指定元素
     *
     * @return 返回当前对象
     * @see List#removeAll(Collection)
     */
    public ListWrapper<E> without(E... elements) {
        list.retainAll(Arrays.asList(elements));
        return this;
    }

    /**
     * 删除指定元素
     *
     * @return 返回当前对象
     * @see List#removeAll(Collection)
     */
    public ListWrapper<E> without(Collection<? extends E> elements) {
        list.retainAll(elements);
        return this;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        list.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        list.sort(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean equals(Object o) {
        return list.equals(o);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E set(int index, E element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        list.add(index, element);
    }

    @Override
    public E remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<E> spliterator() {
        return list.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return list.removeIf(filter);
    }

    @Override
    public Stream<E> stream() {
        return list.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return list.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        list.forEach(action);
    }

    @Override
    public List<E> unwrap() {
        return list;
    }
}
