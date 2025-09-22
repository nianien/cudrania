package com.cudrania.core.loader;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;

/**
 * 基于文件的类加载器<br/>
 * <ul>
 *     <li>{@link #loadClass(String)}加载类，优先从当前类加载器中进行加载，如果找不到，则尝试从父类加载器中加载，可以指定不被允许从父类加载器加载的特定类</li>
 *     <li>{@link #getResource(String)}/{@link #getResources(String)}加载资源列表，优先从当前类加载器加载资源，如果找不到，则尝试从父类加载器中加载</li>
 * </ul>
 * Created on 2022/10/21
 *
 * @author liyifei
 */
public class FileClassLoader extends URLClassLoader {

    @Getter
    private final File file;

    private String[] excludes;

    /**
     * 加载指定文件
     *
     * @param jarFile            需要加载的class或jar文件
     * @param excludesFromParent 指定不被允许从父类加载器加载的特定类
     */
    public FileClassLoader(File jarFile, String... excludesFromParent) {
        this(jarFile, ClassLoader.getSystemClassLoader(), excludesFromParent);
    }

    /**
     * 加载指定文件
     *
     * @param jarFile            需要加载的class或jar文件
     * @param parentLoader       父类加载器
     * @param excludesFromParent 指定不被允许从父类加载器加载的特定类
     */
    @SneakyThrows
    public FileClassLoader(File jarFile, ClassLoader parentLoader, String... excludesFromParent) {
        super(new URL[]{classPathToURL(jarFile.getPath())}, parentLoader);
        this.file = jarFile;
        this.excludes = excludesFromParent;
    }


    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        //资源扫描时,优先加载当前文件中的资源
        Enumeration<URL> enumeration = findResources(name);
        if (enumeration.hasMoreElements()) {
            return enumeration;
        }
        if (getParent() != null) {
            return getParent().getResources(name);
        }
        return Collections.emptyEnumeration();
    }

    @Override
    public URL getResource(String name) {
        //查找资源时, 优先查找当前文件中的资源
        URL url = findResource(name);
        if (url == null) {
            if (getParent() != null) {
                url = getParent().getResource(name);
            }
        }
        return url;
    }


    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                try {
                    c = findClass(name);
                } catch (ClassNotFoundException e) {
                    //ignore, continue to load from parent
                }
                if (c == null && getParent() != null && Arrays.stream(excludes).noneMatch(name::matches)) {
                    c = getParent().loadClass(name);
                }
            }
            if (c == null) {
                throw new ClassNotFoundException(name);
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }

    }


    @Override
    @SneakyThrows
    public String toString() {
        return getClass() + "@" + Integer.toHexString(super.hashCode()) + "[" + file.getCanonicalPath() + "]";
    }


    @SneakyThrows
    public static URL classPathToURL(String path) {
        File file = new File(path);
        // 获取系统上的规范路径
        String urlString = "file://" + file.getCanonicalPath();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileClassLoader that = (FileClassLoader) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }
}

