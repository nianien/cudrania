package com.cudrania.test.loader;

import com.cudrania.core.loader.CompositeClassLoader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author skyfalling
 */
public class TestLoader {

    @Test
    public void testLoader() {
        CompositeClassLoader ccl = new CompositeClassLoader();
        ccl.add(new File("./src/test/resources/test1.jar"));
        ccl.add(new File("./src/test/resources/test2.jar"));
        System.out.println(ccl.wrapped().unwrap());
        ccl.remove(new File("./src/test/resources/test2.jar"));
        System.out.println(ccl.wrapped().unwrap());
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

    /**
     * 资源查找(Spring配置场景): 加入附加loader后, getResources<b>不得重复枚举</b>同一资源.
     * <p>
     * 附加loader(FileClassLoader)自身child-first且父加载器为system loader, 已能摸到应用classpath资源;
     * 若CompositeClassLoader再沿wrapper链聚合, rootLoader的同一份资源会被重复枚举, 导致Spring
     * 按classpath*:扫描出现重复配置. 这里断言各资源URL唯一, 守护该行为.
     */
    @Test
    @SneakyThrows
    public void testResourceLookupNoDuplicate() {
        CompositeClassLoader ccl = new CompositeClassLoader();
        ccl.add(new File("./src/test/resources/test2.jar"));
        Enumeration<URL> urls = ccl.getResources("db.properties");
        java.util.List<URL> list = java.util.Collections.list(urls);
        java.util.Set<URL> unique = new java.util.HashSet<>(list);
        Assertions.assertEquals(unique.size(), list.size(), "getResources不应重复枚举同一资源: " + list);
    }

    @Test
    @SneakyThrows
    public void testLoader4() {
        Thread.currentThread().getContextClassLoader().loadClass(TestLoader.class.getName());
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
    }


}
