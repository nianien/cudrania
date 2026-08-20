package com.cudrania.test.verify;

import com.cudrania.core.arrays.ArrayUtils;
import com.cudrania.core.bits.BitValue;
import com.cudrania.core.bits.MultiBit;
import com.cudrania.core.collection.map.CaseInsensitiveMap;
import com.cudrania.core.comparator.StringComparator;
import com.cudrania.core.functions.Fn;
import com.cudrania.core.utils.RadixUtils;
import com.cudrania.test.bean.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 对本轮修复的行为性改动做硬断言验证：每条都设计为"在修复前会失败、修复后通过"。
 */
public class TestFixVerification {

    /**
     * StringComparator: 含null排序不抛异常, 且四种排序下null都排在最后。
     * 修复前两分支都返回1, 违反反对称性, TimSort会抛IllegalArgumentException。
     */
    @Test
    public void testStringComparatorNullLast() {
        for (StringComparator c : StringComparator.values()) {
            List<String> list = new ArrayList<>(Arrays.asList("bb", null, "a", null, "cccc", "dd"));
            assertDoesNotThrow(() -> list.sort(c), "排序含null不应抛异常: " + c);
            // null必须都在末尾
            int firstNull = list.indexOf(null);
            for (int i = firstNull; i < list.size(); i++) {
                assertNull(list.get(i), c + " 的null未全部排在最后: " + list);
            }
        }
    }

    /**
     * Fn缓存改为按lambda类为键: 两个不同方法引用必须解析出各自正确的方法名, 不能串味。
     */
    @Test
    public void testFnCacheByClass() {
        String n1 = Fn.of(User::getId).name();
        String n2 = Fn.of(User::getUserId).name();
        assertEquals("getId", n1);
        assertEquals("getUserId", n2);
        assertNotEquals(n1, n2);
        // 再取一次, 命中缓存后仍应正确
        assertEquals("getId", Fn.of(User::getId).name());
    }

    /**
     * ArrayUtils.contains对未排序数组必须正确。修复前用binarySearch, 未排序时结果未定义。
     */
    @Test
    public void testArrayContainsUnsorted() {
        int[] unsorted = {5, 3, 9, 1, 7};
        assertTrue(ArrayUtils.contains(unsorted, 9));
        assertTrue(ArrayUtils.contains(unsorted, 5));
        assertTrue(ArrayUtils.contains(unsorted, 7));
        assertFalse(ArrayUtils.contains(unsorted, 8));
        String[] strs = {"banana", "apple", "cherry"};
        assertTrue(ArrayUtils.contains(strs, "apple"));
        assertFalse(ArrayUtils.contains(strs, "x"));
    }

    /**
     * CaseInsensitiveMap.putAll: 修复前putAll走HashMap.putVal, 不维护大小写映射。
     */
    @Test
    public void testCaseInsensitivePutAll() {
        Map<String, String> src = new HashMap<>();
        src.put("UserName", "jack");
        CaseInsensitiveMap<String, String> map = new CaseInsensitiveMap<>();
        map.putAll(src);
        assertEquals("jack", map.get("username"));
        assertEquals("jack", map.get("USERNAME"));
        assertEquals("jack", map.getOrDefault("Username", "x"));
    }

    /**
     * MultiBit: 在index*bitNum>=32的高位读写正确。修复前int移位溢出(掩码回绕)。
     * oct(): bitNum=3, index=11 → 移位量33。
     */
    @Test
    public void testMultiBitHighIndex() {
        MultiBit bit = MultiBit.oct();
        bit.set(11, 5);
        assertEquals(5, bit.get(11), "高位读写应正确(移位量33)");
        bit.set(20, 7);
        assertEquals(7, bit.get(20));
        assertEquals(5, bit.get(11), "写入高位不应破坏其它位");
    }

    /**
     * BitValue边界: index 63合法, 64越界抛异常(修复前index==64,value=false会误清bit0)。
     */
    @Test
    public void testBitValueBoundary() {
        BitValue bv = new BitValue(0);
        bv.set(63, true);
        assertTrue(bv.get(63));
        assertThrows(RuntimeException.class, () -> new BitValue(0).set(64, false));
    }

    /**
     * RadixUtils: "0"用new String构造(非常量池), 修复前用==比较会走不到快速返回, 但结果仍应为0。
     */
    @Test
    public void testRadixZero() {
        assertEquals(0L, RadixUtils.toNumber(new String("0"), 10));
        assertEquals(255L, RadixUtils.toNumber("FF", 16));
    }
}
