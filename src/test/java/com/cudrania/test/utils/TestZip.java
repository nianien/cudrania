package com.cudrania.test.utils;

import java.io.File;

import com.cudrania.core.utils.Zipper;

/**
 * @author skyfalling
 * 
 */
public class TestZip {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
//		Zipper.zip(new File("D:\\Corporation"));
		Zipper.unzip(new File("E:\\JavaWord\\ide\\eclipse-java-juno-SR1-win32-x86_64.zip"),"D:\\Program Files\\eclipse",false);
	}

}
