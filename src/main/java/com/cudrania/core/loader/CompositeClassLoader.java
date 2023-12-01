package com.cudrania.core.loader;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 多个类加载器聚合的classloader，支持动态增删类加载器
 * Created on 2022/10/21
 *
 * @author liyifei
 */
public class CompositeClassLoader extends ClassLoader {

    private final ClassLoader rootLoader;

    /**
     * classloader的集合
     */
    private List<ClassLoader> classLoaders = new ArrayList<>();


    /**
     * 默认使用当前上下文类加载器
     */
    public CompositeClassLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * 指定默认类加载器
     *
     * @param classLoader
     */
    public CompositeClassLoader(ClassLoader classLoader) {
        this.rootLoader = classLoader;
    }

    public Class<?> loadClass(String name)
            throws ClassNotFoundException {
        return wrapped().loadClass(name);
    }


    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return wrapped().getResources(name);
    }

    @Override
    public URL getResource(String name) {
        return wrapped().getResource(name);
    }


    /**
     * 添加指定类加载器
     *
     * @param loader
     * @return
     */
    public CompositeClassLoader add(ClassLoader loader) {
        this.classLoaders.add(loader);
        return this;
    }


    /**
     * 添加文件类加载器
     *
     * @param file
     * @return
     */
    public CompositeClassLoader add(File file) {
        this.add(new FileClassLoader(file));
        return this;
    }

    /**
     * 插入类加载器
     *
     * @param loader
     * @return
     */
    public CompositeClassLoader insert(ClassLoader loader) {
        this.classLoaders.add(0, loader);
        return this;
    }

    /**
     * 插入文件类加载器
     *
     * @param file
     * @return
     */
    public CompositeClassLoader insert(File file) {
        this.insert(new FileClassLoader(file));
        return this;
    }


    /**
     * 移除指定类加载器
     *
     * @param loader
     * @return
     */
    @SneakyThrows
    public CompositeClassLoader remove(ClassLoader loader) {
        this.classLoaders.remove(loader);
        return this;
    }


    /**
     * 移除文件类加载器
     *
     * @param file
     * @return
     */
    @SneakyThrows
    public CompositeClassLoader remove(File file) {
        this.remove(new FileClassLoader(file));
        return this;
    }

    /**
     * 获取加载器的包装对象
     *
     * @return
     */
    public ClassLoaderWrapper wrapped() {
        return ClassLoaderWrapper.of(rootLoader, classLoaders.toArray(new ClassLoader[0]));
    }


    /**
     * 类加载器的包装类
     */
    public static class ClassLoaderWrapper extends ClassLoader {

        private ClassLoader classloader;

        public ClassLoaderWrapper(ClassLoader classloader, ClassLoader parent) {
            super(parent);
            if (classloader == null) {
                throw new NullPointerException("the wrapped classloader cannot be null!");
            }
            this.classloader = classloader;
        }


        /**
         * 返回原始ClassLoader
         *
         * @return
         */
        public ClassLoader unwrap() {
            if (this.classloader instanceof ClassLoaderWrapper) {
                return ((ClassLoaderWrapper) this.classloader).unwrap();
            }
            return this.classloader;
        }



        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return this.classloader.loadClass(name);
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return classloader.getResources(name);
        }

        @Override
        public URL getResource(String name) {
            return classloader.getResource(name);
        }


        private static ClassLoaderWrapper of(ClassLoader rootLoader, ClassLoader... classLoaders) {
            ClassLoaderWrapper wrapper = new ClassLoaderWrapper(rootLoader, null);
            for (ClassLoader classLoader : classLoaders) {
                wrapper = new ClassLoaderWrapper(classLoader, wrapper);
            }
            return wrapper;
        }

    }


}

