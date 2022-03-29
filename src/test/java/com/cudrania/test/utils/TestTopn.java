package com.cudrania.test.utils;

import com.cudrania.algorithm.PriorityHeap;
import com.cudrania.core.io.Files;
import com.cudrania.core.loader.ResourceLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class TestTopn {

    @Test
    public void test() throws IOException {
        List<String> list = Files.readLines(ResourceLoader.getFile("all200000.txt"));
        PriorityHeap<String> heap = PriorityHeap.of(list.size(), String.class);

        for (String s : list) {
            heap.add(s);
        }
        String[] arr = heap.popAll();

//		System.out.println(Arrays.toString(arr));
        Collections.sort(list);
//		System.out.println(Arrays.toString(list.toArray(new String[0])));
        int n = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(arr.length - i - 1).equals(arr[i])) {
                n++;
            }
        }

        System.out.println(n + "/" + list.size());
    }
}
