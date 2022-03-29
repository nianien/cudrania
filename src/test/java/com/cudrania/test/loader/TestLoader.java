package com.cudrania.test.loader;

import com.cudrania.core.loader.FilePathClassLoader;
import org.junit.jupiter.api.Test;

/**
 * @author skyfalling
 * 
 */
public class TestLoader {

	@Test
	public void testLoader() throws Exception {
		ClassLoader loader = new FilePathClassLoader("./target/test-classes");
		Class<?> cl = loader.loadClass(TestLoader.class.getName());
		cl.getMethod("test0").invoke(null);
	}

	public static void test0() {
		System.out.println("hello,world");
	}

}
