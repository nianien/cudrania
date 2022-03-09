package com.cudrania.test.utils;

import com.cudrania.algorithm.LoserTree;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class TestLoserTree {

	@Test
	public void test() {
		Comparator<Integer> cmpr = new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return -o1.compareTo(o2);
			}
		};
		List<Integer> list1 = new ArrayList<Integer>();
		list1.add(13);
		list1.add(25);
		list1.add(37);
		list1.add(59);
		Collections.sort(list1, cmpr);
		List<Integer> list2 = new ArrayList<Integer>();
		list2.add(12);
		list2.add(16);
		list2.add(28);
		list2.add(30);
		Collections.sort(list2, cmpr);
		List<Integer> list3 = new ArrayList<Integer>();
		list3.add(13);
		list3.add(25);
		list3.add(37);
		list3.add(49);
		Collections.sort(list3, cmpr);
		List<Iterator<Integer>> list = new ArrayList<Iterator<Integer>>();
		list.add(list1.iterator());
		list.add(list2.iterator());
		list.add(list3.iterator());
		LoserTree<Integer> ls = new LoserTree<Integer>(list, cmpr);
		Integer n;
		while ((n = ls.pop()) != null)
			System.out.print(n + " ");
	}

}
