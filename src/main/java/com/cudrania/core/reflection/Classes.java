package com.cudrania.core.reflection;

import com.cudrania.core.io.Files;
import lombok.SneakyThrows;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * 获取类相关信息的工具类
 *
 * @author skyfalling
 */
public class Classes {

    /**
     * 获取指定类文件的URL地址<p>
     *
     * @param clazz
     * @return URL
     */
    @SneakyThrows
    public static URL getClassURL(Class<?> clazz) {
        String classPath = clazz.getName().replace('.', '/') + ".class";
        return clazz.getClassLoader().getResource(classPath);
    }

    /**
     * 获取指定类文件所在路径<p>
     * 如果打包,则返回jar所在路径
     *
     * @param clazz
     * @return clazz文件地址
     */
    @SneakyThrows
    public static String getClassLocation(Class<?> clazz) {
        return Files.urlToPath(getClassURL(clazz));
    }

    /**
     * 获取指定类所在目录的根目录,即最外层包所在目录<p>
     * 如果打包,则返回jar所在路径
     *
     * @param clazz
     * @return clazz文件的根目录
     */
    @SneakyThrows
    public static String getClassRoot(Class<?> clazz) {
        ProtectionDomain pd;
        CodeSource cs;
        URL url = (pd = clazz.getProtectionDomain()) == null ? null : (cs = pd.getCodeSource()) == null ? null : cs
                .getLocation();
        return Files.urlToPath(url);
    }

}
