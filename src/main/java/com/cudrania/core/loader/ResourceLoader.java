package com.cudrania.core.loader;

import com.cudrania.core.exception.ExceptionChecker;
import com.cudrania.core.utils.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * ClassPath相关的资源加载类
 *
 * @author skyfalling
 */
public class ResourceLoader {

    /**
     * 获取packageName包中的资源名称
     *
     * @param resourceName
     * @param packageName
     * @return URL
     */
    public static String packageResource(String resourceName, String packageName) {
        return StringUtils.isEmpty(packageName) ? resourceName : packageName.replace('.', '/').concat("/").concat(resourceName);
    }

    /**
     * 查找ClassPath中指定名称的资源,返回其URL<br>
     *
     * @param resourceName
     * @return URL
     */
    public static URL findResource(String resourceName) {
        return findResource(resourceName, null);
    }

    /**
     * 查找packageName包中指定名称的资源,返回其URL<br>
     *
     * @param resourceName
     * @param packageName
     * @return URL
     */
    public static URL findResource(String resourceName, String packageName) {
        return Thread.currentThread().getContextClassLoader().getResource(packageResource(resourceName, packageName));
    }

    /**
     * 查找ClassPath中指定名称的资源,返回其File对象 <br>
     *
     * @param resourceName
     * @return File
     */
    public static File getFile(String resourceName) {
        return getFile(resourceName, null);
    }

    /**
     * 查找packageName包中指定名称的资源,返回其File对象 <br>
     *
     * @param resourceName
     * @param packageName
     * @return File
     */
    public static File getFile(String resourceName, String packageName) {
        try {
            URL url = findResource(resourceName, packageName);
            return url != null ? new File(url.toURI()) : null;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 查找ClassPath中指定名称的资源,返回其InputStream对象 <br>
     * 资源应存在于ClassPath指定的路径中,不包括子目录
     *
     * @param resourceName
     * @return InputStream
     */
    public static InputStream getInputStream(String resourceName) {
        return getInputStream(resourceName, null);
    }

    /**
     * 查找ClassPath中指定名称的资源,返回其InputStream对象 <br>
     * 资源应存在于ClassPath指定的路径中,不包括子目录
     *
     * @param resourceName
     * @param packageName
     * @return InputStream
     */
    public static InputStream getInputStream(String resourceName, String packageName) {
        try {
            URL url = findResource(resourceName, packageName);
            return url != null ? url.openStream() : null;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 查找ClassPath中指定名称的资源,返回其文件路径 <br>
     *
     * @param resourceName
     * @return String
     */
    public static String getPath(String resourceName) {
        return getPath(resourceName, null);
    }

    /**
     * 查找packageName包中指定名称的资源,返回其文件路径 <br>
     *
     * @param resourceName
     * @param packageName
     * @return String
     */
    public static String getPath(String resourceName, String packageName) {
        URL url = findResource(resourceName, packageName);
        return url != null ? url.getFile() : null;
    }

}
