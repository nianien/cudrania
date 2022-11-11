package com.cudrania.test.loader;

import com.cudrania.core.loader.CompositeClassLoader;
import com.cudrania.core.loader.FileClassLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author skyfalling
 * 
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
		CompositeClassLoader ccl=new CompositeClassLoader();
		ccl.add(new File("./src/test/resources/test.jar"));
		ccl.add(new File("./target/test-classes"));
		Class testAClass1=ccl.loadClass("com.test.jar.TestA");
		testAClass1.getDeclaredMethod("test").invoke(testAClass1.getDeclaredConstructor().newInstance());
		ccl.remove(new File("./src/test/resources/test.jar"));
		Assertions.assertThrows(ClassNotFoundException.class,()->ccl.loadClass("com.test.jar.TestA"));
		ccl.add(new File("./src/test/resources/test2.jar"));
		ccl.add(new File("./src/test/resources/test.jar"));
		Class testAClass2=ccl.loadClass("com.test.jar.TestA");
		Assertions.assertNotEquals(testAClass1,testAClass2);
		testAClass2.getDeclaredMethod("test").invoke(testAClass2.getDeclaredConstructor().newInstance());
		ccl.remove(new File("./src/test/resources/test2.jar"));
		Class testAClass3=ccl.loadClass("com.test.jar.TestA");
		Assertions.assertNotEquals(testAClass1,testAClass3);
		testAClass3.getDeclaredMethod("test").invoke(testAClass3.getDeclaredConstructor().newInstance());
		ccl.remove(new File("./src/test/resources/test.jar"));
		Assertions.assertThrows(ClassNotFoundException.class,()->ccl.loadClass("com.test.jar.TestA"));
	}

	@Test
	public void testLoader3() {
		CompositeClassLoader ccl=new CompositeClassLoader();
		ccl.add(new File("./src/test/resources/test2.jar"));
		ccl.add(new File("./src/test/resources/test.jar"));
		ccl.remove(new File("./src/test/resources/test2.jar"));
		ccl.remove(new File("./src/test/resources/test.jar"));
	}

	public static void test0() {
		System.out.println("hello,world");
	}

}
