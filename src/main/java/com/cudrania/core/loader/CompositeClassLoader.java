package com.cudrania.core.loader;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Stack;

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
    private ClassLoaderWrapper classloader;
    /**
     * 根加载器
     */
    private ClassLoaderWrapper root;

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
            this.classloader = (ClassLoaderWrapper) classLoader;
        } else {
            this.classloader = new ClassLoaderWrapper(classLoader, classLoader.getParent());
        }
        this.root = this.classloader;
    }

    public Class<?> loadClass(String name)
            throws ClassNotFoundException {
        return classloader.loadClass(name);
    }


    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return classloader.getResources(name);
    }

    @Override
    public URL getResource(String name) {
        return classloader.getResource(name);
    }


    /**
     * 添加类加载器加载指定文件,优先级最低
     *
     * @param file
     * @return
     */
    public CompositeClassLoader add(File file) {
        return add(new FileClassLoader(file));
    }

    /**
     * 添加指定类加载器,优先级最低
     *
     * @param loader
     * @return
     */
    public CompositeClassLoader add(ClassLoader loader) {
        this.classloader = this.classloader.add(loader);
        return this;
    }


    /**
     * 添加类加载器加载指定文件,优先级仅次于根加载器
     *
     * @param file
     * @return
     */
    public CompositeClassLoader insert(File file) {
        return insert(new FileClassLoader(file));
    }

    /**
     * 添加指定类加载器,优先级仅次于根加载器
     *
     * @param loader
     * @return
     */
    public CompositeClassLoader insert(ClassLoader loader) {
        this.classloader = this.classloader.insert(loader);
        return this;
    }


    /**
     * 移除指定文件的类加载器
     *
     * @param file
     * @return
     */
    @SneakyThrows
    public CompositeClassLoader remove(File file) {
        return remove(new FileClassLoader(file));
    }


    /**
     * 移除类加载器
     *
     * @param loader
     * @return
     */
    @SneakyThrows
    public CompositeClassLoader remove(ClassLoader loader) {
        this.classloader = this.classloader.remove(loader);
        return this;
    }


    /**
     * 获取当前类加载器的包装对象
     *
     * @return
     */
    public ClassLoaderWrapper wrapped() {
        return this.classloader;
    }


    /**
     * 类加载的包装类
     */
    public class ClassLoaderWrapper extends ClassLoader {

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
                if (!origin.equals(loader)) {
                    cls.push(origin);
                } else {
                    break;
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

