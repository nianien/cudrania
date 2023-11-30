package com.cudrania.core.loader;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;

/**
 * 多个类加载器聚合的classloader，支持动态增删类加载器
 * Created on 2022/10/21
 *
 * @author liyifei
 */
public class CompositeClassLoader extends ClassLoader {

    /**
     * 当前classloader的包装对象
     */
    private ClassLoaderWrapper wrapper;
    /**
     * 根加载器的包装对象
     */
    private ClassLoaderWrapper root;

    /**
     * classloader的集合
     */
    private Map<Object, ClassLoader> classLoaders = new HashMap<>();


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
        if (classLoader instanceof ClassLoaderWrapper) {
            this.wrapper = (ClassLoaderWrapper) classLoader;
        } else {
            this.wrapper = new ClassLoaderWrapper(classLoader, classLoader.getParent());
        }
        this.root = this.wrapper;
    }

    public Class<?> loadClass(String name)
            throws ClassNotFoundException {
        return wrapper.loadClass(name);
    }


    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return wrapper.getResources(name);
    }

    @Override
    public URL getResource(String name) {
        return wrapper.getResource(name);
    }


    /**
     * 添加类加载器,优先级仅次于根加载器
     *
     * @param key
     * @param builder
     * @return
     */
    public <T> CompositeClassLoader add(T key, Function<T, ClassLoader> builder) {
        Map<T, ClassLoader> cls = (Map<T, ClassLoader>) classLoaders;
        cls.computeIfAbsent(key, k -> {
            ClassLoader classLoader = builder.apply(k);
            CompositeClassLoader.this.add(classLoader);
            return classLoader;
        });
        return this;
    }

    /**
     * 添加指定类加载器,优先级最低
     *
     * @param loader
     * @return
     */
    public CompositeClassLoader add(ClassLoader loader) {
        this.wrapper = this.wrapper.add(loader);
        return this;
    }


    /**
     * 添加类加载器,优先级仅次于根加载器
     *
     * @param key
     * @param clsLoader
     * @return
     */
    public <T> CompositeClassLoader insert(T key, Function<T, ClassLoader> clsLoader) {
        Map<T, ClassLoader> cls = (Map<T, ClassLoader>) classLoaders;
        cls.computeIfAbsent(key, k -> {
            ClassLoader classLoader = clsLoader.apply(k);
            CompositeClassLoader.this.insert(classLoader);
            return classLoader;
        });
        return this;
    }

    /**
     * 添加类加载器,优先级仅次于根加载器
     *
     * @param loader
     * @return
     */
    public CompositeClassLoader insert(ClassLoader loader) {
        this.wrapper = this.wrapper.insert(loader);
        return this;
    }


    /**
     * 移除指定类加载器
     *
     * @param key
     * @return
     */
    @SneakyThrows
    public <T> CompositeClassLoader remove(T key) {
        classLoaders.computeIfPresent(key, (k, v) -> {
            CompositeClassLoader.this.remove(v);
            return null;
        });
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
        if (classLoaders.values().contains(loader)) {
            this.wrapper = this.wrapper.remove(loader);
            this.classLoaders.remove(loader);
        }
        return this;
    }


    /**
     * 获取当前类加载器
     *
     * @return
     */
    public ClassLoader get() {
        return this.wrapper.unwrap();
    }


    /**
     * 类加载的包装类
     */
    private class ClassLoaderWrapper extends ClassLoader {

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

        /**
         * 添加classloader,作为最后一个classloader的子classloader
         *
         * @param loader
         * @return
         */
        private ClassLoaderWrapper add(ClassLoader loader) {
            return new ClassLoaderWrapper(loader, this);
        }


        /**
         * 插入classloader,作为根classloader的子classloader
         *
         * @param loader
         * @return
         */
        private ClassLoaderWrapper insert(ClassLoader loader) {
            Stack<ClassLoader> cls = new Stack<>();
            ClassLoader cl = this;
            while (cl != root && cl != null) {
                ClassLoader origin = ((ClassLoaderWrapper) cl).unwrap();
                cls.push(origin);
                cl = cl.getParent();
            }
            if (cl != null) {
                cls.push(cl);
            }
            //here parent loader must be not null
            ClassLoader parent = new ClassLoaderWrapper(loader, cls.pop());
            while (!cls.empty()) {
                parent = new ClassLoaderWrapper(cls.pop(), parent);
            }
            return parent instanceof ClassLoaderWrapper ? (ClassLoaderWrapper) parent : new ClassLoaderWrapper(parent, null);
        }

        /**
         * 移除classloader
         *
         * @param loader
         * @return
         */
        @SneakyThrows
        private ClassLoaderWrapper remove(ClassLoader loader) {
            Stack<ClassLoader> cls = new Stack<>();
            ClassLoader cl = this;
            while (cl != root && cl != null) {
                ClassLoader origin = ((ClassLoaderWrapper) cl).unwrap();
                cl = cl.getParent();
                if (origin == loader) {
                    break;
                } else {
                    cls.push(origin);
                }
            }
            if (cl != null) {
                cls.push(cl);
            }
            //here parent loader must be not null
            ClassLoader parent = cls.pop();
            while (!cls.empty()) {
                parent = new ClassLoaderWrapper(cls.pop(), parent);
            }
            return parent instanceof ClassLoaderWrapper ? (ClassLoaderWrapper) parent : new ClassLoaderWrapper(parent, null);
        }


        protected Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                // First, check if the class has already been loaded
                Class<?> c = findLoadedClass(name);
                if (c == null) {
                    try {
                        if (getParent() != null) {
                            c = getParent().loadClass(name);
                        }
                    } catch (ClassNotFoundException e) {
                        // ClassNotFoundException thrown if class not found
                        // from the non-null parent class loader
                    }
                    if (c == null && this.classloader != null) {
                        c = this.classloader.loadClass(name);
                    }
                }
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            }
        }


        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return classloader.getResources(name);
        }

        @Override
        public URL getResource(String name) {
            return classloader.getResource(name);
        }

    }


}

