package com.cudrania.test.tree;

import com.cudrania.core.collection.map.Pair;
import com.cudrania.core.tree.TreeBuilder;
import com.cudrania.core.tree.TreeNode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import static org.junit.Assert.assertTrue;

/**
 * @author skyfalling
 */
public class TestTreeBuilder {

    @Data
    static class Triple {

    }


    @Test
    public void test() {


        Map<Pair, Pair> map = new HashMap();
        map.put(new Pair(101, "娱乐综合"), new Pair(1, "音乐影视"));
        map.put(new Pair(102, "在线音乐"), new Pair(1, "音乐影视"));
        map.put(new Pair(103, "宽带电影"), new Pair(1, "音乐影视"));
        map.put(new Pair(104, "视频短片"), new Pair(1, "音乐影视"));
        map.put(new Pair(105, "网络电视"), new Pair(1, "音乐影视"));


        map.put(new Pair(201, "笑话"), new Pair(2, "休闲娱乐"));
        map.put(new Pair(202, "图片"), new Pair(2, "休闲娱乐"));
        map.put(new Pair(203, "动漫"), new Pair(2, "休闲娱乐"));
        map.put(new Pair(204, "flash"), new Pair(2, "休闲娱乐"));
        map.put(new Pair(206, "flash"), new Pair(2, "休闲娱乐"));

        map.put(new Pair(20211, "笑话"), new Pair(202, "休闲娱乐"));
        map.put(new Pair(20301, "图片"), new Pair(203, "休闲娱乐"));
        map.put(new Pair(20415, "动漫"), new Pair(204, "休闲娱乐"));

        TreeNode<Pair> tree = TreeBuilder.buildTree(map, Pair::getKey);
        TreeBuilder.traversal(tree, node -> System.out.println(node));
        TreeBuilder.minimize(tree, node -> {
            if (node.data() != null) {
                Pair pair = node.data();
                Object key = pair.getKey();
                return key.equals(20415) || key.equals(105);
            }
            return false;
        });

        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeBuilder.traversal(tree, (node) -> {
            treeNodes.add(node);
            System.out.println(node);
        });

        assertTrue(treeNodes.size() == 6);
    }
}
