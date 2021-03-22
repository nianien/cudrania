package com.cudrania.core.io;

import com.cudrania.core.exception.ExceptionChecker;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 扫描指定包名的Class对象,可指定类过滤器,只加载符合要求的Class对象
 *
 * @author skyfalling
 */
public class ClassScanner {

    private ClassLoader classLoader;
    /**
     * 类过滤器
     */
    private Predicate<Class> classFilter;

    /**
     * 构造方法,默认扫描所有类型
     */
    public ClassScanner() {
        this(targetClass -> true);
    }

    /**
     * 构造方法,指定的类过滤器
     *
     * @param classFilter
     */
    public ClassScanner(Predicate<Class> classFilter) {
        this(Thread.currentThread().getContextClassLoader(), classFilter);
    }

    /**
     * 构造方法,指定类过滤器和类加载器
     *
     * @param classLoader
     * @param classFilter
     */
    public ClassScanner(ClassLoader classLoader, Predicate<Class> classFilter) {
        this.classFilter = classFilter;
        this.classLoader = classLoader;
    }

    public List<Class> scan(String packageName) {

        // 扫描class类的集合
        List<Class> classes = new ArrayList<Class>();
        // 是否循环迭代
        // 获取包的名字 并进行替换
        String packagePath = packageName.replace('.', '/');
        try {
            // 定义一个枚举的集合 并进行循环来处理这个目录下的things
            Enumeration<URL> dirs = classLoader.getResources(
                    packagePath);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equalsIgnoreCase(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    scanClassByPath(packageName, filePath, classes);
                } else if ("jar".equalsIgnoreCase(protocol)) {
                    // 如果是jar文件
                    JarFile jar = ((JarURLConnection) url.openConnection())
                            .getJarFile();
                    // 迭代遍历jar包中的类
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        // 获取jar里的一个实体
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        // 如果是以/开头的
                        if (name.charAt(0) == '/') {
                            // 获取后面的字符串
                            name = name.substring(1);
                        }
                        // 如果前半部分和定义的包名相同
                        if (name.startsWith(packagePath)) {
                            // 如果是一个.class文件且不是目录
                            if (name.endsWith(".class")
                                    && !entry.isDirectory()) {
                                // 去掉".class"获取类名
                                String className = name.replace('/', '.').substring(0, name
                                        .length() - 6);
                                loadClass(className, classes);
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            ExceptionChecker.throwException(e);
        }
        return classes;
    }

    /**
     * 扫描指定包路径下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param classes
     */
    private void scanClassByPath(String packageName,
                                 String packagePath, Collection<Class> classes) {
        // 获取包的目录
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //获取包下的所有文件,包括目录
        File[] files = dir.listFiles(new FileFilter() {
            // 自定义过滤规则,目录或者class文件
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".class");
            }
        });
        // 循环所有文件
        for (File file : files) {
            // 如果是目录,递归遍历
            if (file.isDirectory()) {
                scanClassByPath(packageName + ""
                                + file.getName(), file.getAbsolutePath(),
                        classes);
            } else {
                // 如果是class文件,加载类
                String className = packageName + '.' + file.getName().substring(0,
                        file.getName().length() - 6);
                loadClass(className, classes);
            }
        }
    }

    /**
     * 加载符合要求的类
     *
     * @param className
     * @param classes
     */
    private void loadClass(String className, Collection<Class> classes) {
        try {
            System.out.println("scan Class: " + className);
            Class targetClass = classLoader.loadClass(className);
            if (classFilter.test(targetClass)) {
                System.out.println("load Class: " + targetClass);
                classes.add(targetClass);
            }
        } catch (ClassNotFoundException e) {
            ExceptionChecker.throwException(e);
        }
    }
}
