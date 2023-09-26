package com.cudrania.test.bits;

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
        MultiBit mb1 = MultiBit.hex().set(0, 4);
        MultiBit mb2 = MultiBit.hex().set(1, 5);
        MultiBit mb3 = MultiBit.hex().set(2, 6);
        int n = 1;
        long r1 = mb1.apply(mb2.apply(mb3.apply(n)));
        long r2 = mb3.apply(mb2.apply(mb1.apply(n)));
        System.out.println(Long.toHexString(r1));
        System.out.println(Long.toHexString(r2));
        System.out.println(mb3.apply("n"));
        System.out.println(mb2.apply("n"));
        System.out.println(mb1.apply("n"));
        n=n & -241 | 80;
        n=n & -3841 | 1536;
        n=n & -16 | 4;
        System.out.println(Long.toHexString(n));
    }

}
