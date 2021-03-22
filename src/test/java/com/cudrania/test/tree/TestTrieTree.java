package com.cudrania.test.tree;

import com.cudrania.core.io.Files;
import com.cudrania.core.tree.TrieTree;
import com.cudrania.core.utils.TimeCounter;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class TestTrieTree {


    private static List<String> alist;
    private static List<String> slist;


    @BeforeClass
    public static void setUp() throws IOException {
        alist = Files.readLines(TestTrieTree.class.getClassLoader().getResource("all200000.txt").openStream());
        slist = Files.readLines(TestTrieTree.class.getClassLoader().getResource("search20000.txt").openStream());
    }

    @Test
    public void testDisplay() {
        Consumer h = node -> System.out.println(node);
        TrieTree tree = new TrieTree();
        tree.insert("a");
        tree.insert("b");
        tree.insert("c");
        tree.insert("ab");
        tree.insert("bd");
        tree.insert("abc");
        tree.insert("abcd");
        tree.delete("a");
        tree.delete("ab");
        tree.delete("abc");
        tree.delete("abcd");
        System.out.println(tree.display());
        System.out.println("=======================");
        tree.visit(h);
        System.out.println("=======================");
        tree.optimize();
        System.out.println(tree.display());
        System.out.println("=======================");
        tree.visit(h);
    }


    @Test
    public void testSuggest() {
        String data = "155";
        TrieTree tree = new TrieTree();
        for (String str : alist) {
            tree.insert(str);
        }
        TimeCounter tc = new TimeCounter();
        tc.start();
        int i = 0;
        for (String str : alist) {
            if (!data.equals(str) && str.startsWith(data))
                i++;
        }
        tc.stop();
        System.out.println("alist used: " + tc.timePassed() + " ms, suggest: "
                + i);

        tc.start();
        List<String> suggest = tree.suggest(data);
        tc.stop();
        System.out.println("trie tree used: " + tc.timePassed()
                + " ms, suggest: " + suggest.size());

        tree.optimize();
        tc.start();
        suggest = tree.suggest(data);
        tc.stop();
        System.out.println("optimized tree used: " + tc.timePassed()
                + " ms, suggest: " + suggest.size());

    }


    @Test
    public void testSearch() {

        TrieTree tree = new TrieTree();
        HashMap<String, String> map = new HashMap<String, String>();
        for (String s : alist) {
            tree.insert(s);
            map.put(s, s);
        }

        TimeCounter tc = new TimeCounter();
        // map搜索
        System.out.println("==>search by map...");
        int i = 0;
        tc.start();
        for (String s : slist) {
            if (map.containsKey(s))
                i++;
        }
        tc.stop();
        System.out.println("map used :" + tc.timePassed() + " ms, find: " + i);


        // list搜索
        System.out.println("==>search by list...");
        i = 0;
        tc.start();
        for (String s : slist) {
            if (alist.contains(s))
                i++;
        }
        tc.stop();
        System.out.println("list used: " + tc.timePassed() + " ms, find: " + i);


        // trie tree搜索
        System.out.println("==>search by tree...");
        i = 0;
        tc.start();
        for (String s : slist) {
            if (tree.find(s))
                i++;
        }
        tc.stop();
        System.out.println("trie tree used: " + tc.timePassed() + " ms, find: "
                + i);

        // 优化后的树
        tree.optimize();
        System.out.println("==>search by optimized tree...");
        i = 0;
        tc.start();
        for (String s : slist) {
            if (tree.find(s))
                i++;
        }
        tc.stop();
        System.out.println("optimized tree used: " + tc.timePassed()
                + " ms, find: " + i);


        // 再次优化，应无变化
        tree.optimize();
        System.out.println("==>search by optimized tree...");
        i = 0;
        tc.start();
        for (String s : slist) {
            if (tree.find(s))
                i++;
        }
        tc.stop();
        System.out.println("optimized again  used: " + tc.timePassed()
                + " ms, find: " + i);

    }
}
