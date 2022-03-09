package com.cudrania.test.utils;

import com.cudrania.core.collection.map.CounterMap;
import com.cudrania.algorithm.ConsistentHash;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class TestConsistentHash {


    /**
     * 测试平衡性
     */
    @Test
    public void testBalance() {
        List<String> keys = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5"));
        Collection<String> data = getAllData(100000);
        test(data, keys, null);
        keys.remove("2");
        test(data, keys, null);
    }


    /**
     * 测试命中率
     */
    @Test
    public void testHit() {
        List<String> keys = new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e"));
        Collection<String> data = new HashSet(getAllData(100000));
        MultiMap multiMap = new MultiValueMap();
        test(data, keys, multiMap);
        keys.remove("2");
        test(data, keys, multiMap);
        int n = 0;
        for (Object key : multiMap.keySet()) {
            List list = (List) multiMap.get(key);
            if (list.size() > 1 && list.get(0).equals(list.get(1))) {
                n++;
            }/* else {
                System.out.println(list);
            }*/
        }
        System.out.println("hit percent: " + (n * 100.00 / data.size()) + "%");
    }


    private static void test(Collection<String> list, List<String> keys, MultiMap multiMap) {
        CounterMap<String> counterMap = new CounterMap<String>();
        ConsistentHash<String> hash = new ConsistentHash<>(keys);
        for (String data : list) {
            //判断data在哪个节点上, target is one of keys
            String target = hash.getTarget(data);
            counterMap.increase(target);
            if (multiMap != null)
                multiMap.put(data, target);
        }
        System.out.println("\nkeys count : " + keys.size() + ", list count : "
                + list.size() + ", Normal percent : " + (float) 100
                / keys.size() + "%");
        System.out.println("-------------------- boundary  ----------------------");
        for (Entry<String, Integer> entry : counterMap.entrySet()) {
            System.out.println("key :" + entry.getKey() + " - Times : "
                    + entry.getValue() + " - Percent : "
                    + (float) entry.getValue() / list.size() * 100 + "%");
        }
    }

    /**
     * @param size
     * @return
     */
    private static List<String> getAllData(int size) {
        List<String> list = new ArrayList<String>(size);
        for (int i = 0; i < size; i++) {
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            // 随机产生20到30位的字符串
            for (int j = 0; j < random.nextInt(20) + 10; j++) {
                sb.append((char) (random.nextInt(95) + 32));
            }
            list.add(sb.toString());
        }
        return list;
    }

}
