package com.cudrania.test.loader;

import com.cudrania.core.loader.CompositeClassLoader;
import com.cudrania.core.loader.FileClassLoader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;

/**
 * @author skyfalling
 */
public class TestLoader {

    @Test
    public void testLoader() throws Exception {
        ClassLoader loader = new FileClassLoader(new File("./target/test-classes"));
        Class<?> cl = loader.loadClass(TestLoader.class.getName());
        cl.getMethod("test0").invoke(null);
    }


    @Test
    public void testLoader2() throws Exception {
        CompositeClassLoader ccl = new CompositeClassLoader();
        ccl.add(new File("./src/test/resources/test.jar"));
        ccl.add(new File("./target/test-classes"));
        Class testAClass1 = ccl.loadClass("com.test.jar.TestA");
        testAClass1.getDeclaredMethod("test").invoke(testAClass1.getDeclaredConstructor().newInstance());
        ccl.remove(new File("./src/test/resources/test.jar"));
        Assertions.assertThrows(ClassNotFoundException.class, () -> ccl.loadClass("com.test.jar.TestA"));
        ccl.add(new File("./src/test/resources/test2.jar"));
        ccl.add(new File("./src/test/resources/test.jar"));
        Class testAClass2 = ccl.loadClass("com.test.jar.TestA");
        Assertions.assertNotEquals(testAClass1, testAClass2);
        testAClass2.getDeclaredMethod("test").invoke(testAClass2.getDeclaredConstructor().newInstance());
        ccl.remove(new File("./src/test/resources/test2.jar"));
        Class testAClass3 = ccl.loadClass("com.test.jar.TestA");
        Assertions.assertNotEquals(testAClass1, testAClass3);
        testAClass3.getDeclaredMethod("test").invoke(testAClass3.getDeclaredConstructor().newInstance());
        ccl.remove(new File("./src/test/resources/test.jar"));
        Assertions.assertThrows(ClassNotFoundException.class, () -> ccl.loadClass("com.test.jar.TestA"));
    }

    @Test
    @SneakyThrows
    public void testLoader3() {
        CompositeClassLoader ccl = new CompositeClassLoader();
        ccl.insert(new File("./src/test/resources/test2.jar"));
        Class<?> aClass = Class.forName("com.test.jar.TestA", false, ccl);
        System.out.println(aClass.getClassLoader());
        ccl.remove(new File("./src/test/resources/test2.jar"));
        Class<?> bClass = Class.forName("com.test.jar.TestA", false, ccl);
        Assertions.assertThrows(ClassNotFoundException.class, () -> ccl.loadClass("com.test.jar.TestA"));
        ccl.add(new File("./src/test/resources/test.jar"));
        System.out.println("@@@@@@@");
        Class<?> cClass = ccl.loadClass("com.test.jar.TestA");
        System.out.println(bClass.getClassLoader());
        System.out.println(cClass.getClassLoader());
        Assertions.assertEquals(aClass, bClass);
        Assertions.assertNotEquals(bClass, cClass);
    }

    @Test
    @SneakyThrows
    public void testLoader4() {
        CompositeClassLoader ccl = new CompositeClassLoader();
        ccl.add(new File("./src/test/resources/test2.jar"));
        Class<?> aClass = Class.forName("com.test.jar.TestA", false, ccl.wrapped());
        System.out.println(aClass.getClassLoader());
        ccl.remove(new File("./src/test/resources/test2.jar"));
        ccl.add(new File("./src/test/resources/test.jar"));
        Class<?> bClass = Class.forName("com.test.jar.TestA", false, ccl.wrapped());
        Class<?> cClass = ccl.loadClass("com.test.jar.TestA");
        System.out.println(bClass.getClassLoader());
        System.out.println(cClass.getClassLoader());
        Assertions.assertNotEquals(aClass, bClass);
        Assertions.assertEquals(bClass, cClass);
        Thread.currentThread().setContextClassLoader(ccl.wrapped());
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        Assertions.assertNotNull(engine);
        System.out.println(engine);
    }

    public static void test0() {
        System.out.println("hello,world");
    }

}
