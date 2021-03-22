package com.cudrania.core.reflection;

import com.cudrania.core.io.Files;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import static com.cudrania.core.exception.ExceptionChecker.throwException;

/**
 * 获取类相关信息的工具类
 *
 * @author skyfalling
 */
public class Classes {

    /**
     * 获取指定类文件的URL地址<br/>
     *
     * @param clazz
     * @return URL
     */
    public static URL getClassURL(Class<?> clazz) {
        try {
            String classPath = clazz.getName().replace('.', '/') + ".class";
            return clazz.getClassLoader().getResource(classPath);
        } catch (Exception e) {
            throw throwException(e);
        }
    }

    /**
     * 获取指定类文件所在路径<br/>
     * 如果打包,则返回jar所在路径
     *
     * @param clazz
     * @return clazz文件地址
     */
    public static String getClassLocation(Class<?> clazz) {
        try {
            return Files.urlToPath(getClassURL(clazz));
        } catch (Exception e) {
            throw throwException(e);
        }
    }

    /**
     * 获取指定类所在目录的根目录,即最外层包所在目录<br/>
     * 如果打包,则返回jar所在路径
     *
     * @param clazz
     * @return clazz文件的根目录
     */
    public static String getClassRoot(Class<?> clazz) {
        try {
            ProtectionDomain pd;
            CodeSource cs;
            URL url = (pd = clazz.getProtectionDomain()) == null ? null : (cs = pd.getCodeSource()) == null ? null : cs
                    .getLocation();
            return Files.urlToPath(url);
        } catch (Exception e) {
            throw throwException(e);
        }
    }

}
