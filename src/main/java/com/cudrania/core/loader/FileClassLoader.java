package com.cudrania.core.loader;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

/**
 * 基于文件的类加载器<p/>
 * <ul>
 *     <li>类加载时满足双亲委派原则</li>
 *     <li>资源加载时仅加载当前文件资源</li>
 *     <li>资源查找时优先查找当前文件</li>
 * ul/>
 * Created on 2022/10/21
 *
 * @author liyifei
 */
public class FileClassLoader extends URLClassLoader {

    @Getter
    private File file;


    /**
     * 加载指定文件
     *
     * @param file
     */
    public FileClassLoader(File file) {
        this(file, ClassLoader.getSystemClassLoader());
    }

    /**
     * 加载指定文件,设置父加载器
     *
     * @param file
     * @param parent
     */
    @SneakyThrows
    public FileClassLoader(File file, ClassLoader parent) {
        super(new URL[]{classPathToURL(file.getPath())}, parent);
        this.file = file;
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
    public Enumeration<URL> getResources(String name) throws IOException {
        //资源扫描时,不查找父加载器中的资源
        return findResources(name);
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

    @Override
    @SneakyThrows
    public String toString() {
        return getClass() + "@" + Integer.toHexString(super.hashCode()) + "[" + file.getCanonicalPath() + "]";
    }
}

