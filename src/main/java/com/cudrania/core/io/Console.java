package com.cudrania.core.io;

import com.cudrania.core.exception.ExceptionChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * IO控制类
 *
 * @author skyfalling
 */
public class Console {

	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * 从控制台读数据，换行结束
	 *
	 * @return 读取的字符串
	 */
	public static String readLine() {
		try {
			return reader.readLine();
		} catch (Exception e) {
            throw ExceptionChecker.throwException(e);
		}
	}

	/**
	 * 打印字符串,然后从控制台读取数据,自动换行
	 *
	 * @param str
	 * @return 读取的字符串
	 */
	public static String readLine(String str) {
		return readLine(str, false);
	}

	/**
	 * 打印字符串,然后从控制台读取数据.
	 *
	 * @param str
	 * @param inLine 表示打印和读取是否在同一行
	 * @return 读取的字符串
	 */
	public static String readLine(String str, boolean inLine) {
		try {
			if (inLine) {
				System.out.print(str);
			} else {
				System.out.println(str);
			}
			return reader.readLine();
		} catch (IOException e) {
            throw ExceptionChecker.throwException(e);
		}
	}

	/**
	 * 从控制台读数据
	 *
	 * @return 读取的字符个数
	 */
	public static int read() {
		try {
			return reader.read();
		} catch (IOException e) {
            throw ExceptionChecker.throwException(e);
		}
	}

	/**
	 * 向控制台写数据，换行
	 *
	 * @param <T>
	 * @param t
	 */
	public static <T> void writeLine(T t) {
		System.out.println(t);
	}

	/**
	 * 向控制台写数据，换行
	 *
	 * @param <T>
	 */
	public static <T> void writeLine() {
		System.out.println();
	}

	/**
	 * 向控制台写数据，不换行
	 *
	 * @param <T>
	 * @param t
	 */
	public static <T> void write(T t) {
		System.out.print(t);
	}
}
