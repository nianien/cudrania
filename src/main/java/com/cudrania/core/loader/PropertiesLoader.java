package com.cudrania.core.loader;

import com.cudrania.core.exception.ExceptionChecker;
import com.cudrania.core.io.Closer;

import java.io.*;
import java.util.Properties;

/**
 * 加载Properties对象的工具类
 *
 * @author skyfalling
 */
public class PropertiesLoader {

    /**
     * 加载properties文件
     *
     * @param file 文件对象
     * @return Properties对象
     */
    public static Properties load(File file) {
        try {
            return load(new FileInputStream(file));
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }


    /**
     * 加载properties文件,并指定编码
     *
     * @param file    文件对象
     * @param charset 字符编码
     * @return Properties对象
     */
    public static Properties load(File file, String charset) {
        try {
            return load(new InputStreamReader(new FileInputStream(file), charset));
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 加载properties文件
     *
     * @param resource 资源文件
     * @return Properties对象
     */
    public static Properties load(String resource) {
        return load(ResourceLoader.getInputStream(resource));
    }

    /**
     * 加载properties文件
     *
     * @param resource 资源文件
     * @param charset  字符编码
     * @return Properties对象
     */
    public static Properties load(String resource, String charset) {
        try {
            return load(new InputStreamReader(ResourceLoader.getInputStream(resource), charset));
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 加载properties文件
     *
     * @param inputStream 输入流对象
     * @return Properties对象
     */
    public static Properties load(InputStream inputStream) {
        try {
            Properties p = new Properties();
            p.load(inputStream);
            return p;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        } finally {
            Closer.close(inputStream);
        }
    }

    /**
     * 加载properties文件
     *
     * @param reader
     * @return Properties对象
     */
    public static Properties load(Reader reader) {
        try {
            Properties p = new Properties();
            p.load(reader);
            return p;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        } finally {
            Closer.close(reader);
        }
    }


    /**
     * 加载XML文件
     *
     * @param file 文件对象
     * @return Properties对象
     */
    public static Properties loadXML(File file) {
        try {
            return loadXML(new FileInputStream(file));
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 加载XML文件
     *
     * @param resource 资源文件
     * @return Properties对象
     */
    public static Properties loadXML(String resource) {
        return loadXML(ResourceLoader.getInputStream(resource));
    }

    /**
     * 加载XML文件
     *
     * @param inputStream 输入流
     * @return Properties对象
     */
    public static Properties loadXML(InputStream inputStream) {
        InputStream is = null;
        try {
            Properties p = new Properties();
            p.loadFromXML(inputStream);
            return p;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        } finally {
            Closer.close(is);
        }
    }

}
