package com.nianien.cudrania.test.tree;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import com.nianien.cudrania.core.tree.TreeBuilder;
import com.nianien.cudrania.core.tree.TreeNode;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author skyfalling
 */
public class TestTreeBuilder {

    @Data
    static class Triple {

    }


    @Test
    public void test() {


        Map<Entry, Entry> map = new HashMap();
        map.put(new SimpleEntry<>(101, "娱乐综合"), new SimpleEntry<>(1, "音乐影视"));
        map.put(new SimpleEntry<>(102, "在线音乐"), new SimpleEntry<>(1, "音乐影视"));
        map.put(new SimpleEntry<>(103, "宽带电影"), new SimpleEntry<>(1, "音乐影视"));
        map.put(new SimpleEntry<>(104, "视频短片"), new SimpleEntry<>(1, "音乐影视"));
        map.put(new SimpleEntry<>(105, "网络电视"), new SimpleEntry<>(1, "音乐影视"));


        map.put(new SimpleEntry<>(201, "笑话"), new SimpleEntry<>(2, "休闲娱乐"));
        map.put(new SimpleEntry<>(202, "图片"), new SimpleEntry<>(2, "休闲娱乐"));
        map.put(new SimpleEntry<>(203, "动漫"), new SimpleEntry<>(2, "休闲娱乐"));
        map.put(new SimpleEntry<>(204, "flash"), new SimpleEntry<>(2, "休闲娱乐"));
        map.put(new SimpleEntry<>(206, "flash"), new SimpleEntry<>(2, "休闲娱乐"));

        map.put(new SimpleEntry<>(20211, "笑话"), new SimpleEntry<>(202, "休闲娱乐"));
        map.put(new SimpleEntry<>(20301, "图片"), new SimpleEntry<>(203, "休闲娱乐"));
        map.put(new SimpleEntry<>(20415, "动漫"), new SimpleEntry<>(204, "休闲娱乐"));

        TreeNode<Entry> tree = TreeBuilder.buildTree(map, Entry::getKey);
        TreeBuilder.traversal(tree, node -> System.out.println(node));
        TreeBuilder.minimize(tree, node -> {
            if (node.data() != null) {
                Entry pair = node.data();
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
