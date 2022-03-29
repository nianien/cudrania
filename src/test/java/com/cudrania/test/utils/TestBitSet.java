package com.cudrania.test.utils;


import com.cudrania.core.bits.MultiBitSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBitSet {

    @Test
    public void test() {
        MultiBitSet nb = new MultiBitSet(5);
        nb.set(1, 5, 5);
        nb.set(12, 21);
        nb.set(8, 5);
        for (int i = 0; i < 16; i++) {
            System.out.println("[" + i + "]=" + nb.get(i));
        }
        nb.clear(2, 8);
        System.out.println(">>>>>>clear");
        for (int i = 0; i < 16; i++) {
            System.out.println("[" + i + "]=" + nb.get(i));
        }
        System.out.println(">>>>>>length");
        System.out.println(nb.length());
        Assertions.assertEquals(nb.length(), 13);
        System.out.println(">>>>>>size");
        System.out.println(nb.size());
        Assertions.assertEquals(nb.size(), 8);
        System.out.println(">>>>>cardinality>");
        System.out.println(nb.cardinality());
        Assertions.assertEquals(nb.cardinality(), 3);
        System.out.println(">>>>>nextSetBit>");
        System.out.println(nb.nextSetBit(5));
        Assertions.assertEquals(nb.nextSetBit(5), 8);
    }
}
