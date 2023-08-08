package com.cudrania.test.utils;

import com.cudrania.core.utils.Enums;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author skyfalling
 */
public class TestEnums {

    enum Goat {
        A('A'), B('B'), C('C'), D('D'), E('E');
        int value;

        Goat(int v) {
            this.value = v;
        }
    }

    @Test
    public void test() {
        assert Enums.with(TimeUnit.class, "C0", 1) == null;
        assert Enums.with(Goat.class, "value", (int) 'A') == Goat.A;
    }
}
