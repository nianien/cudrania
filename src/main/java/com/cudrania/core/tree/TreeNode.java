package com.cudrania.core.tree;

import java.util.Objects;

/**
 * 树形结构节点定义<br/>
 * 采用孩子兄弟链表表示法
 *
 * @param <T>
 */
public class TreeNode<T> {

    /**
     * 父节点
     */
    private TreeNode<T> parent;
    /**
     * 第一个孩子节点
     */
    private TreeNode<T> firstChild;
    /**
     * 布尔值标记
     */
    private boolean exist;
    /**
     * 下一个兄弟节点
     */
    private TreeNode<T> nextBrother;
    /**
     * 节点数据
     */
    private T data;

    /**
     * 构造方法,指定节点数据
     *
     * @param data
     */
    public TreeNode(T data) {
        this(data, true);
    }

    /**
     * 构造方法
     *
     * @param data  节点数据
     * @param exist 是否标记节点数据
     */
    public TreeNode(T data, boolean exist) {
        this.data = data;
        this.exist = exist;
    }


    /**
     * 获取父节点
     *
     * @return parent
     */
    public TreeNode<T> parent() {
        return this.parent;
    }

    /**
     * 添加孩子节点
     *
     * @param node
     */
    public TreeNode<T> addChild(TreeNode<T> node) {
        if (node == null)
            return this;
        TreeNode child = this.firstChild;
        if (child != null) {
            while (child.nextBrother != null) {
                child = child.nextBrother;
            }
            child.nextBrother = node;
        } else {
            this.firstChild = node;
        }
        node.parent = this;
        return this;
    }

    /**
     * 获取第一个孩子节点
     *
     * @return children
     */
    public TreeNode<T> firstChild() {
        return this.firstChild;
    }


    /**
     * 设置第一个孩子节点
     *
     * @param firstChild
     */
    public void firstChild(TreeNode firstChild) {
        this.firstChild = firstChild;
        if (firstChild != null)
            firstChild.parent = this;
    }


    /**
     * 最后一个孩子节点
     *
     * @return
     */
    public TreeNode<T> lastChild() {
        TreeNode lastChild = this.firstChild;
        if (lastChild != null) {
            while (lastChild.nextBrother != null) {
                lastChild = lastChild.nextBrother;
            }
        }
        return lastChild;
    }


    /**
     * 添加兄弟节点
     *
     * @param treeNode
     */
    public TreeNode<T> addBrother(TreeNode<T> treeNode) {
        if (treeNode == null)
            return this;
        TreeNode brother = this.nextBrother;
        if (brother != null) {
            while (brother.nextBrother != null) {
                brother = brother.nextBrother;
            }
            brother.nextBrother = treeNode;
        } else {
            this.nextBrother = treeNode;
        }
        treeNode.parent = this.parent;
        return this;
    }

    /**
     * 设置下一个兄弟节点
     *
     * @param treeNode
     */
    public void nextBrother(TreeNode treeNode) {
        this.nextBrother = treeNode;
        if (treeNode != null) {
            treeNode.parent = this.parent;
        }
    }

    /**
     * 获取下一个兄弟节点
     *
     * @return
     */
    public TreeNode<T> nextBrother() {
        return nextBrother;
    }

    /**
     * 最后一个兄弟节点
     *
     * @return
     */
    public TreeNode<T> lastBrother() {
        TreeNode lastBrother = this.nextBrother;
        if (lastBrother != null) {
            while (lastBrother.nextBrother != null) {
                lastBrother = lastBrother.nextBrother;
            }
        }
        return lastBrother;
    }

    /**
     * 设置节点是否存在
     *
     * @param value
     */
    public void exist(boolean value) {
        this.exist = value;
    }

    /**
     * 节点是否存在
     *
     * @return
     */
    public boolean exist() {
        return exist;
    }

    /**
     * 获取节点数据
     *
     * @return
     */
    public T data() {
        return data;
    }

    /**
     * 设置节点数据
     *
     * @param value
     */
    public void data(T value) {
        this.data = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreeNode<?> treeNode = (TreeNode<?>) o;

        return Objects.equals(data, treeNode.data);
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }

    /**
     * 显示节点数据内容
     */
    @Override
    public String toString() {
        return data + "[" + exist + "]";
    }

}
