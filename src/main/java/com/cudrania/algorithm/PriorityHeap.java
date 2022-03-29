package com.cudrania.algorithm;

import java.lang.reflect.Array;
import java.util.Comparator;

/**
 * 基于优先级且具有固定容量的小顶堆, 顶元素优先级最低, 用来筛选优先级最高的TopN个元素<br>
 *
 * @param <T>
 * @author skyfalling
 */

public class PriorityHeap<T> {
    /**
     * 存储元素的数组
     */
    private T[] heap;
    /**
     * 堆容量
     */
    private int capacity;
    /**
     * 实际元素数量
     */
    private int size;

    /**
     * 元素比较对象
     */
    private Comparator<T> comparator;
    /**
     * 元素类型
     */
    private Class<T> elementType;


    /**
     * 指定堆的容量和元素类型
     *
     * @param capacity
     * @param elementType
     * @param <T>
     * @return
     */
    public static <T extends Comparable<T>> PriorityHeap<T> of(int capacity, Class<T> elementType) {
        return new PriorityHeap<>(capacity, elementType, Comparable::compareTo);
    }


    /**
     * 构造方法, 指定堆的容量和元素比较对象
     *
     * @param capacity    堆容量
     * @param elementType 元素类型
     * @param comparator  元素比较器
     */
    public PriorityHeap(int capacity, Class<T> elementType, Comparator<T> comparator) {
        this.capacity = capacity;
        this.elementType = elementType;
        this.heap = arrayOfElements(elementType, capacity);
        this.comparator = comparator;
    }

    /**
     * 添加元素, 该操作只有当堆未满或者待添加元素优先级高于堆顶元素时才会执行
     *
     * @param element
     * @return 返回当前元素和堆顶元素优先级较低者
     */
    public T add(T element) {
        if (size < capacity) {
            heap[size] = element;
            size++;
            upHeap();
            return heap[0];
        } else if (lower(heap[0], element)) {
            T lowest = heap[0];
            heap[0] = element;
            downHeap();
            return lowest;
        } else {
            return element;
        }
    }

    /**
     * 获取堆顶元素, 该元素一定是堆内优先级最低的元素
     *
     * @return
     */
    public T peek() {
        return heap[0];
    }

    /**
     * 获取并移除堆顶元素
     *
     * @return
     */
    public T pop() {
        if (size == 0)
            return null;
        // 堆顶元素
        T top = heap[0];
        // 堆尾元素置顶
        heap[0] = heap[size - 1];
        // 堆尾置空
        heap[size - 1] = null;
        size--;
        // 调整堆
        downHeap();
        return top;
    }

    /**
     * 不断移除堆顶元素到数组中, 直到堆为空, 数组中的元素将按照优先级从高到低的顺序排列<br>
     *
     * @return
     */
    public T[] popAll() {
        T[] result = arrayOfElements(elementType, size);
        while (size > 0) {
            result[size - 1] = pop();
        }
        return result;
    }

    /**
     * 堆内实际元素数量
     *
     * @return
     */
    public int size() {
        return size;
    }

    /**
     * 清空堆内元素
     */
    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    /**
     * 堆尾追加元素时, 从该元素的父节点向上调整堆结构
     */
    private void upHeap() {
        // 子节点
        int child = size - 1;
        // 堆尾元素
        T node = heap[child];
        // 父节点
        int parent = (child - 1) >>> 1;
        while (child > 0 && lower(node, heap[parent])) {
            heap[child] = heap[parent];
            child = parent;
            parent = (child - 1) >>> 1;
        }
        heap[child] = node;
    }

    /**
     * 移除堆顶元素时, 将堆尾元素置顶, 然后向下调整堆结构
     */
    private void downHeap() {
        // 父节点
        int parent = 0;
        // 堆顶元素
        T top = heap[parent];
        // 左孩子节点
        int left = (parent << 1) + 1;
        if (left + 1 < size && lower(heap[left + 1], heap[left])) {
            left++;
        }
        while (left < size && lower(heap[left], top)) {
            heap[parent] = heap[left];
            parent = left;
            left = (parent << 1) + 1;
            if (left + 1 < size && lower(heap[left + 1], heap[left])) {
                left++;
            }
        }
        heap[parent] = top;
    }

    /**
     * 判断元素t1的优先级是否低于t2<br>
     *
     * @param t1
     * @param t2
     * @return
     */
    private boolean lower(T t1, T t2) {
        return comparator.compare(t1, t2) < 0;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] arrayOfElements(Class<T> elementType, int size) {
        return (T[]) Array.newInstance(elementType, size);
    }


}
