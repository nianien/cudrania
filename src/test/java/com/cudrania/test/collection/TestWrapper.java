package com.cudrania.test.collection;

import com.cudrania.core.collection.wrapper.ListWrapper;
import com.cudrania.core.collection.wrapper.MapWrapper;
import com.cudrania.core.collection.wrapper.SetWrapper;
import com.cudrania.core.collection.wrapper.Wrappers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class TestWrapper {

    @Test
    public void testMap() {
        MapWrapper<String, Integer> wrapper = Wrappers.map("a", 1).$put("b", 2).$put("c", 3);
        Map<String, Integer> map = wrapper.$this();
        Assertions.assertEquals(map.getClass(), HashMap.class);
        Assertions.assertTrue(map.containsKey("a") && map.containsKey("b") && map.containsKey("c"));
        Wrappers.map(wrapper).$remove("a").$remove("b");
        Assertions.assertTrue(map.size() == 1);
        Wrappers.map(map).$clear();
        Assertions.assertTrue(map.isEmpty());
    }


    @Test
    public void testList() {
        ListWrapper<String> wrapper = Wrappers.list("a").$add("b").$add("c");
        wrapper.doBatch(2, System.out::println);
        List<String> list = wrapper.$this();
        Assertions.assertEquals(list.getClass(), ArrayList.class);
        Assertions.assertTrue(list.contains("a") && list.contains("b") && list.contains("c"));
        Wrappers.list(list).$remove("a");
        Assertions.assertTrue(list.size() == 2);
        Wrappers.list(list).$clear();
        Assertions.assertTrue(list.isEmpty());
    }


    @Test
    public void testSet() {
        SetWrapper<String> wrapper = Wrappers.set("a").$add("b").$add("c").$add("c");
        wrapper.doBatch(2, System.out::println);
        System.out.println(wrapper.map(a->"$"+a));
        Set<String> set = wrapper.$this();
        Assertions.assertEquals(set.getClass(), HashSet.class);
        Assertions.assertTrue(set.contains("a") && set.contains("b") && set.contains("c"));
        Wrappers.set(set).$remove("a");
        Assertions.assertTrue(set.size() == 2);
        Wrappers.set(set)
                .$clear();
        Assertions.assertTrue(set.isEmpty());
    }
}
