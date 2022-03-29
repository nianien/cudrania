package com.cudrania.core.tree;


import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 构造树的工具类<p>
 * 树结构采用孩子兄弟链表表示法
 *
 * @author skyfalling
 */
public class TreeBuilder {

    /**
     * 构建树形结构<p>
     *
     * @param list         节点数据列表,数据节点需要有唯一标识和父节点标识
     * @param idGenerator  获取当前节点标识的函数
     * @param pIdGenerator 获取父节点标识的函数
     * @param <T>
     * @return 返回一个虚拟根节点作为实际数据的根节点的根节点
     */
    public static <T> TreeNode buildTree(Collection<T> list, Function<T, Object> idGenerator, Function<T, Object> pIdGenerator) {
        // 虚拟根节点
        TreeNode<T> root = new TreeNode<>(null);
        // 全部节点的映射表
        Map<Object, TreeNode<T>> trees = new HashMap<>();
        for (T t : list) {
            putIfAbsent(trees, t, idGenerator);
        }
        for (TreeNode<T> node : trees.values()) {
            Object pId = pIdGenerator.apply(node.data());
            if (trees.containsKey(pId)) {
                trees.get(pId).addChild(node);
            } else {
                root.addChild(node);
            }
        }
        return root;
    }


    /**
     * 构建树形结构<p>
     *
     * @param relation    父子关系图,key表示当前节点数据,value表示父节点数据,数据节点需要有唯一标识
     * @param idGenerator 获取当前节点标识的函数
     * @return 返回一个虚拟根节点作为实际数据的根节点的根节点
     */
    public static <T> TreeNode buildTree(Map<T, T> relation, Function<T, Object> idGenerator) {
        //数据映射表
        Map<Object, T> dataMap = new HashMap<>();
        //关系映射表
        Map<Object, Object> relationMap = new HashMap<>();
        for (Entry<T, T> entry : relation.entrySet()) {
            Object id = idGenerator.apply(entry.getKey());
            Object pid = idGenerator.apply(entry.getValue());
            relationMap.put(id, pid);
            dataMap.put(id, entry.getKey());
            dataMap.put(pid, entry.getValue());
        }
        return buildTree(dataMap.values(), idGenerator, (v) -> relationMap.get(idGenerator.apply(v)));
    }


    /**
     * 最小化树结构,只保留符合条件的节点及其父节点<br>
     * 如果某个节点满足选择条件,那么从该节点通往根节点的路径将被保留,到叶节点的路径被裁剪
     *
     * @param tree
     * @param selector
     * @return 当前节点是否保留
     */
    public static <T> boolean minimize(TreeNode<T> tree, Predicate<TreeNode<T>> selector) {
        //采取后根顺序从底自上进行最小化裁剪
        if (tree == null)
            return false;
        TreeNode<T> child = tree.firstChild();
        boolean flag = false;
        while (child != null) {
            //裁剪当前孩子节点
            flag |= minimize(child, selector);
            //裁剪下一个孩子节点
            child = child.nextBrother();
        }
        //判断当前节点是否保留
        flag |= selector.test(tree);
        if (!flag && tree.parent() != null) {
            //当前节点需要被裁剪
            TreeNode<T> parent = tree.parent();
            if (parent.firstChild() == tree) {
                //如果当前节点是第一个孩子节点,则只需将当前节点的下一个兄弟节点前移即完成裁剪
                parent.firstChild(tree.nextBrother());
            } else {
                //如果不是第一个孩子节点,则需要从第一个孩子节点进行遍历
                // 将当前节点的前一个兄弟节点连接后一个兄弟节点完成裁剪
                child = tree.parent().firstChild();
                while (child != null && child.nextBrother() != tree) {
                    child = child.nextBrother();
                }
                if (child != null) {
                    child.nextBrother(tree.nextBrother());
                }
            }
        }
        return flag;
    }


    /**
     * 层次遍历树
     *
     * @param tree
     * @param consumer
     * @param <T>
     */
    public static <T> void traversal(TreeNode<T> tree, Consumer<TreeNode<T>> consumer) {
        List<TreeNode> list = new ArrayList<TreeNode>();
        list.add(tree);
        while (!list.isEmpty()) {
            TreeNode node = list.remove(0);
            consumer.accept(node);
            if (node.firstChild() != null) {
                list.add(node.firstChild());
            }
            TreeNode brother = node.nextBrother();
            while (brother != null) {
                consumer.accept(brother);
                if (brother.firstChild() != null) {
                    list.add(brother.firstChild());
                }
                brother = brother.nextBrother();
            }
        }
    }

    /**
     * 将指定数据的节点加入映射表中,如果已经存在,则不再加入
     *
     * @param trees
     * @param value
     * @param keyGen
     * @return
     */
    private static <T> TreeNode<T> putIfAbsent(Map<Object, TreeNode<T>> trees,
                                               T value, Function<T, Object> keyGen) {
        Object key = keyGen.apply(value);
        TreeNode node = trees.get(key);
        if (node == null) {
            // 如果父节点不存在,创建父节点
            node = new TreeNode(value);
            // 将该节点放入映射表中
            trees.put(key, node);
        }
        if (value != null) {
            node.data(value);
        }
        return node;
    }


}
