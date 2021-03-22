package com.cudrania.test.log;

import com.cudrania.core.log.LoggerFactory;

import org.junit.Test;

import java.util.logging.Logger;

public class TestLog {

	private static Logger LOG1 = LoggerFactory.getLogger("a.b");
	private static Logger LOG2 = LoggerFactory.getLogger("a.b.c");

	@Test
	public void testLog() throws Exception {
//		LoggerFactory.setLevel(Level.WARNING);
		LOG1.info("dddd");
		LOG1.warning("dddd");
		LOG1.severe("dddd");
//		LoggerFactory.setLevel(Level.INFO);

		System.err.println("================");
		LOG2.info("dddd");
		LOG2.warning("dddd");
		LOG2.severe("dddd");

	}

}
