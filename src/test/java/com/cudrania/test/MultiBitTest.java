package com.cudrania.test;

import com.cudrania.core.bits.MultiBit;
import org.junit.jupiter.api.Test;

/**
 * 一个bit只能表示0和1两种状态, 这里使用n个bit表示一位,每位可以表示2^n-1种状态
 *
 * @author scorpio
 * @version 1.0.0
 */
public class MultiBitTest {

    @Test
    public void test() {
        MultiBit mb = MultiBit.hex().set(14, 15).set(13, 14);
        assert mb.toString().equals("fe00000");
        assert mb.apply(1) == 0xfe00001;
        System.out.println(Long.toString(mb.apply(1), 16));
    }

}
