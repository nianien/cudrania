package com.cudrania.test.reflect;

import com.cudrania.core.reflection.Methods;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author skyfalling.
 */
public class TestMethods {

    /**
     * caller depth 语义(实现已由 getStackTrace 魔数偏移迁移到 StackWalker):
     * depth 0 = 直接调用 getCallerXxx 的方法;depth 1 = 再上一层。
     */
    @Test
    public void testCallerDepth() {
        // depth 0 = 本方法 testCallerDepth
        assertEquals(TestMethods.class.getName(), Methods.getCallerClassName(0));
        assertEquals("testCallerDepth", Methods.getCallerMethodName(0));
        // getCurrentClassName/MethodName 返回调用它的方法 = 本方法
        assertEquals(TestMethods.class.getName(), Methods.getCurrentClassName());
        assertEquals("testCallerDepth", Methods.getCurrentMethodName());
    }

    @Test
    public void testCallerFromNested() {
        nested();
    }

    private void nested() {
        // 在 nested 内:depth 0 = nested,depth 1 = 调用它的 testCallerFromNested
        assertEquals("nested", Methods.getCallerMethodName(0));
        assertEquals("testCallerFromNested", Methods.getCallerMethodName(1));
    }
}
