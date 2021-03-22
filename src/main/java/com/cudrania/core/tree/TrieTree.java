package com.cudrania.core.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 字典树,支持增删查找以及树结构的优化<br>
 * 这里对标准字典树进行改造,将节点数据不存在的无分支路径进行垂直合并
 *
 * @author skyfalling
 */
public class TrieTree {

    /**
     * 根节点,不存储数据
     */
    private TreeNode<String> root = new TreeNode<String>("`");

    /**
     * 查找数据
     *
     * @param data
     * @return
     */
    public boolean find(String data) {
        TreeNode firstChild = root.firstChild();
        if (firstChild == null)
            return false;
        TreeNode node = find(firstChild, data);
        return node != null ? node.exist() : false;

    }

    /**
     * 插入数据
     *
     * @param data
     * @return
     */
    public void insert(String data) {
        TreeNode firstChild = root.firstChild();
        if (firstChild == null) {
            root.firstChild(new TreeNode(data, true));
        } else {
            insert(firstChild, data);
        }
    }

    /**
     * 删除数据
     *
     * @param data
     */
    public void delete(String data) {
        TreeNode firstChild = root.firstChild();
        if (firstChild == null)
            return;
        TreeNode node = find(firstChild, data);
        if (node != null)
            node.exist(false);
    }

    /**
     * 推荐以指定数据为前缀的数据
     *
     * @param data
     * @return
     */
    public List<String> suggest(String data) {
        List<String> list = new ArrayList<String>();
        TreeNode firstChild = root.firstChild();
        if (firstChild != null) {
            TreeNode node = suggest(firstChild, data);
            if (node != null)
                paths(node, list, new StringBuilder(data));
        }
        return list;
    }

    /**
     * 字典树结构优化
     */
    public void optimize() {
        // 根节点不能被合并
        TreeNode firstChild = root.firstChild();
        if (firstChild != null) {
            this.optimize(root);
        }
    }

    /**
     * 层次遍历访问节点
     *
     * @param consumer
     */
    public void visit(Consumer<TreeNode<String>> consumer) {
        TreeNode child = root.firstChild();
        if (child == null)
            return;
        TreeBuilder.traversal(child, consumer);
    }

    /**
     * 字典树的图形显示,用于调试分析<br>
     * 当节点较多时,调用该方法可能会造成堆栈溢出错误
     */
    public String display() {
        List<TreeNode> list = new ArrayList<TreeNode>();
        StringBuilder sb = new StringBuilder();
        list.add(root);
        TreeNode tail = list.get(0);
        while (!list.isEmpty()) {
            TreeNode node = list.remove(0);
            if (node == null) {
                sb.append("-|");
                continue;
            }
            sb.append(node);
            list.add(node.firstChild());
            TreeNode brother = node.nextBrother();
            while (brother != null) {
                sb.append("→").append(brother);
                list.add(brother.firstChild());
                brother = brother.nextBrother();
            }
            if (node != tail) {
                sb.append("|");
                continue;
            }
            while (!list.isEmpty()) {
                tail = list.get(list.size() - 1);
                if (tail != null)
                    break;
                list.remove(list.size() - 1);
            }
            if (!list.isEmpty()) {
                sb.append("\n↓\n");
            }
        }
        return sb.toString();
    }

    /**
     * 从指定节点开始插入数据
     *
     * @param node
     * @param data
     * @return
     */
    private void insert(TreeNode<String> node, String data) {
        String value = node.data();
        if (data.equals(value)) { // 插入数据存在
            node.exist(true);
            return;
        }
        int index = mixed(data, value);
        if (index == 0) {// 没有公共开始部分,将数据插入兄弟节点
            TreeNode brother = node.nextBrother();
            if (brother != null) {
                insert(brother, data);
            } else {
                node.nextBrother(new TreeNode(data, true));
            }
        } else {
            if (value.length() > index) {// 节点数据多于公共部分,则节点进行纵向分裂
                // 将多余部分插入孩子节点
                TreeNode temp = new TreeNode(value.substring(index),
                        node.exist());
                TreeNode child = node.firstChild();
                // 将原节点的孩子节点变为孙子节点
                if (child != null) {
                    temp.firstChild(child);
                }
                // 原节点的数据更新为公共部分
                node.data(value.substring(0, index));
                // 公共部分是否为插入数据
                node.exist(node.data().equals(data));
                node.firstChild(temp);
            }
            if (data.length() > index) {// 插入数据多于公共部分,则多余部分插入孩子节点
                TreeNode firstChild = node.firstChild();
                if (firstChild == null) {// 如果没有孩子节点,则创建孩子节点
                    node.firstChild(new TreeNode(data.substring(index), true));
                } else {// 否则直接插入孩子节点
                    insert(firstChild, data.substring(index));
                }
            }
        }
    }

    /**
     * 从指定节点开始查找数据
     *
     * @param node
     * @param data
     */
    private TreeNode find(TreeNode<String> node, String data) {
        String value = node.data();
        if (data.equals(value)) { // 查询数据存在
            return node;
        }
        int index = mixed(data, value);
        if (index == 0) {// 不存在公共部分,查询兄弟节点
            TreeNode brother = node.nextBrother();
            return brother != null ? find(brother, data) : null;
        }
        TreeNode firstChild = node.firstChild();
        // 节点数据为查询数据的前缀,则继续查询子节点
        return data.startsWith(value) && firstChild != null ? find(firstChild,
                data.substring(value.length())) : null;
    }

    /**
     * 从指定节点开始优化字典树结构,节点数据不存在的无分支路径进行垂直合并
     *
     * @param node
     */
    private void optimize(TreeNode<String> node) {
        TreeNode child = node.firstChild();
        if (child == null)
            return;
        // 只有一个孩子节点,且父子节点数据都不存在
        if (!node.exist() && !child.exist() && child.nextBrother() == null) {
            // 合并内容
            node.data(node.data() + child.data());
            // 孙子节点变为孩子节点
            node.firstChild(child.firstChild());
            optimize(node);
        } else {
            optimize(node.firstChild());
        }
    }

    /**
     * 两个字符串公共开始部分的长度
     *
     * @param str1
     * @param str2
     * @return
     */
    private static int mixed(String str1, String str2) {
        int len = str1.length() < str2.length() ? str1.length() : str2.length();
        int index = -1;
        for (int i = 0; i < len; i++) {
            if (str1.charAt(i) != str2.charAt(i))
                break;
            index = i;
        }
        return index + 1;
    }

    /**
     * 查询以指定字符串为前缀且相似度最大的字符串<br>
     * 该方法返回一个虚拟节点,节点的数据为不包含指定数据后的剩余部分<br>
     * 该节点的孩子节点为查找到的节点的孩子节点
     *
     * @param node
     * @param data
     * @return
     */
    private TreeNode suggest(TreeNode<String> node, String data) {
        String value = node.data();
        if (value.startsWith(data)) {
            TreeNode<String> temp = new TreeNode(value.substring(data.length()));
            temp.firstChild(node.firstChild());
            temp.exist(node.exist() && !temp.data().isEmpty());
            return temp;
        }
        int index = mixed(data, value);
        if (index == 0) {// 不存在公共部分,查询兄弟节点
            TreeNode brother = node.nextBrother();
            return brother != null ? suggest(brother, data) : null;
        }
        TreeNode firstChild = node.firstChild();
        // 节点数据为查询数据的前缀,则继续查询子节点
        return data.startsWith(value) && firstChild != null ? suggest(firstChild,
                data.substring(value.length())) : null;
    }

    /**
     * 获取指定节点存在的全部子路径
     *
     * @param node
     * @param list
     * @param sb
     */
    private void paths(TreeNode<String> node, List<String> list, StringBuilder sb) {
        sb.append(node.data());
        // 节点存在,加入路径
        if (node.exist())
            list.add(sb.toString());
        if (node.firstChild() != null) {
            paths(node.firstChild(), list, sb);
        }
        // 回退该节点,查找兄弟节点
        sb.delete(sb.length() - node.data().length(), sb.length());
        if (node.nextBrother() != null) {
            paths(node.nextBrother(), list, sb);
        }
    }
}
