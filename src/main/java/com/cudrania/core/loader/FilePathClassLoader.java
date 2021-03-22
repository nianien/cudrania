package com.cudrania.core.loader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import com.cudrania.core.exception.ExceptionChecker;

/**
 * 按文件路径进行加载的类加载器
 * 
 * @author skyfalling
 * 
 */
public class FilePathClassLoader extends URLClassLoader {

	/**
	 * 构造方法,字符串数组指定classpath<br>
	 * 
	 * @param paths
	 */
	public FilePathClassLoader(String[] paths) {
		super(getUrls(paths));
	}

	/**
	 * 构造方法,classpath以路径分隔符分开<br>
	 * 在windows下分隔符为";",在Unix、Linux下分隔符为":"
	 * 
	 * @param classpath
	 */
	public FilePathClassLoader(String classpath) {
		this(classpath.split(File.pathSeparator));
	}

	/**
	 * 将指定的classpath转换为URL
	 * 
	 * @param path
	 * @return URL
	 * @throws Exception
	 */
	private static URL classPathToURL(String path) throws Exception {
		File file = new File(path);
		// 获取系统上的规范路径
		String urlString = "file:/" + file.getCanonicalPath();
		if (file.isFile()) {
			if (path.toLowerCase().endsWith("zip")) {
				urlString = "zip:" + urlString + "!/";
			} else if (path.toLowerCase().endsWith("jar")) {
				urlString = "jar:" + urlString + "!/";
			}
		} else {
			urlString = urlString + "/";
		}
		return new URL(urlString);
	}

	/**
	 * 将标识路径集合的字符串数组转换成URL数组
	 * 
	 * @param paths
	 * @return URL数组
	 */
	private static URL[] getUrls(String[] paths) {
		try {
			URL[] urls = new URL[paths.length];
			int i = 0;
			for (String path : paths) {
				urls[i++] = classPathToURL(path);
			}
			return urls;
		} catch (Exception e) {
            throw ExceptionChecker.throwException(e);
		}
	}

}
