package com.cudrania.test.reflect;

import org.junit.jupiter.api.Test;

import com.cudrania.core.reflection.Methods;

/**
 * @author skyfalling.
 */
public class TestMethods {

    @Test
    public void testCurrentMethod() {
        demo(0);
        demo(1);
        demo(2);
    }

    private void demo(int depth) {
        System.out.println(Methods.getCallerClassName(depth) + "#" + Methods.getCallerMethodName(depth));
    }
}
