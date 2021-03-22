package com.cudrania.core.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 败者树,对多个有序的数据源进行归并排序<br>
 *
 * @param <T>
 * @author skyfalling
 */
public class LoserTree<T> {
    /**
     * 非叶子节点, 记录数据源的索引位置, 根据节点的值可以定位到所指向的数据源
     */
    private int[] tree;
    /**
     * 叶子节点, 叶子节点和数据源是一一对应的, 即第一个叶子节点记录第一个数据源的当前数据
     */
    private Object[] nodes;
    /**
     * 数据源列表,为叶子节点提供数据, 数据源的输出顺序必须有序且和元素的比较结果保持一致
     */
    private Iterator<T>[] branches;
    /**
     * 叶子节点数据的比较对象
     */
    private Comparator<T> comparator;

    /**
     * 构造方法,按照元素的Comparable接口实现进行排序
     */
    public LoserTree(List<Iterator<T>> branches) {
        this(branches, (o1, o2) -> ((Comparable<T>) o1).compareTo(o2));
    }

    /**
     * 构造方法, 指定数据源分支的迭代器和元素比较对象<br>
     * 迭代器的输出必须有序并且与Comparator对象的比较结果保持一致
     *
     * @param branches
     * @param comparator
     */
    @SuppressWarnings("unchecked")
    public LoserTree(List<Iterator<T>> branches, Comparator<T> comparator) {
        this.branches = branches.toArray(new Iterator[0]);
        this.comparator = comparator;
        this.init();
    }

    /**
     * 依次读取数据源的数据进行归并排序, 返回排序后的数据列表<br>
     *
     * @return
     */
    public List<T> merge() {
        List<T> list = new ArrayList<T>();
        T top = null;
        while ((top = get(tree[0])) != null) {
            list.add(top);
            put(tree[0]);
            adjust(tree[0]);
        }
        return list;
    }

    /**
     * 获取并移除当前冠军节点<br>
     *
     * @return
     */
    public T pop() {
        T result = get(tree[0]);
        if (result != null) {
            put(tree[0]);
            adjust(tree[0]);
        }
        return result;
    }

    /**
     * 获取当前冠军节点<br>
     *
     * @return
     */
    public T top() {
        return get(tree[0]);
    }

    /**
     * 初始化构建败者树<br>
     */
    private void init() {
        int size = this.branches.length;
        this.tree = new int[size];
        this.nodes = new Object[size];
        // 为叶子节点赋值
        for (int i = 0; i < size; i++) {
            this.put(i);
        }
        int winner = 0;
        for (int i = 1; i < size; i++) {
            if (beat(i, winner)) {
                winner = i;
            }
        }
        // 非叶子节点初始化为冠军节点
        Arrays.fill(tree, winner);
        // 从后向前依次调整非叶子节点
        for (int i = size - 1; i >= 0; i--)
            adjust(i);
    }

    /**
     * 调整第index个叶子节点<br>
     * 具体调整过程为: 叶子节点和父节点比较, 败者留在父节点位置, 胜者继续和父节点的父节点比较,直到整棵树的根节点
     *
     * @param index
     */
    private void adjust(int index) {
        int size = this.branches.length;
        int t = (size + index) / 2;
        while (t > 0) {
            // 败者留在父节点的位置
            if (beat(tree[t], index)) {
                int temp = tree[t];
                tree[t] = index;
                index = temp;
            }
            t /= 2;
        }
        tree[0] = index;
    }

    /**
     * 获取第index个叶子节点的当前数据<br>
     * 如果返回null,则表示数据源结束
     *
     * @param index
     * @return
     */
    @SuppressWarnings("unchecked")
    private T get(int index) {
        return (T) nodes[index];
    }

    /**
     * 设置第index个叶子节点的下一个数据<br>
     * 如果数据源已结束,则设置为null
     *
     * @param index
     */
    private void put(int index) {
        Iterator<T> branch = this.branches[index];
        this.nodes[index] = branch.hasNext() ? branch.next() : null;
    }

    /**
     * 判断index1对应的节点是否能打败index2对应的节点
     *
     * @param index1
     * @param index2
     * @return
     */
    private boolean beat(int index1, int index2) {
        T t1 = get(index1);
        T t2 = get(index2);
        if (t1 == null)
            return false;
        if (t2 == null)
            return true;
        // 这里, 当叶节点数据相等时比较分支索引是为了实现排序算法的稳定性
        int n = comparator.compare(t1, t2);
        return n != 0 ? n < 0 : index1 < index2;
    }

}
