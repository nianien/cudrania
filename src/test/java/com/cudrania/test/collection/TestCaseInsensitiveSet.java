package com.cudrania.test.collection;

import com.cudrania.core.collection.set.CaseInsensitiveSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skyfalling
 */
public class TestCaseInsensitiveSet {
    public static void main(String[] args) {

        List<String> list = new ArrayList<String>();
        list.add("A");
        list.add("B");
        CaseInsensitiveSet set = new CaseInsensitiveSet(list);
        list.add("C");
//        System.out.println(set.add("A"));
//        System.out.println(set.add("B"));
        System.out.println(set.add("b1"));
//        System.out.println(set.add("C"));
        //        System.out.println(set.contains("a"));
        //        System.out.println(set.remove("b"));
        //        System.out.println(set.remove("b"));
        System.out.println(set.retainAll(list));
//        System.out.println(set.removeAll(list));
        System.out.println(set);
    }
}
