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

    private ClassLoaderWrapper classLoader;

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
            this.classLoader = (ClassLoaderWrapper) classLoader;
        } else {
            this.classLoader = new ClassLoaderWrapper(classLoader, null);
        }
    }

    public Class<?> loadClass(String name)
            throws ClassNotFoundException {
        return classLoader.loadClass(name);
    }


    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return classLoader.getResources(name);
    }

    @Override
    public URL findResource(String name) {
        return classLoader.getResource(name);
    }


    /**
     * 添加指定文件的类加载器
     *
     * @param file
     * @return
     */
    public CompositeClassLoader add(String file) {
        return add(new File(file));
    }

    /**
     * 添加指定文件的类加载器
     *
     * @param file
     * @return
     */
    public CompositeClassLoader add(File file) {
        return add(new FileClassLoader(file));
    }

    /**
     * 添加类加载器
     *
     * @param loader
     * @return
     */
    public CompositeClassLoader add(ClassLoader loader) {
        this.classLoader = classLoader.add(loader);
        return this;
    }

    /**
     * 移除指定文件的类加载器
     *
     * @param file
     * @return
     */
    @SneakyThrows
    public CompositeClassLoader remove(String file) {
        return remove(new File(file));
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
        this.classLoader = classLoader.remove(loader);
        return this;
    }

    /**
     * 查找指定资源的类加载器
     *
     * @param file
     * @return
     */
    public FileClassLoader find(File file) {
        ClassLoader classLoader = this.classLoader.find(new FileClassLoader(file));
        if (classLoader instanceof FileClassLoader) {
            return (FileClassLoader) classLoader;
        }
        return null;
    }

    /**
     * 获取当前classloader
     *
     * @return
     */
    public ClassLoader get() {
        return this.classLoader.unwrap();
    }


    /**
     * Created on 2022/10/21
     *
     * @author liyifei
     */
    class ClassLoaderWrapper extends ClassLoader {

        private ClassLoader classLoader;

        public ClassLoaderWrapper(ClassLoader classLoader, ClassLoader parent) {
            super(parent);
            if (classLoader == null) {
                throw new NullPointerException("the wrapped classloader cannot be null!");
            }
            this.classLoader = classLoader;
        }


        /**
         * 添加classloader
         *
         * @param loader
         * @return
         */
        public ClassLoaderWrapper add(ClassLoader loader) {
            return new ClassLoaderWrapper(loader, this);
        }


        /**
         * 移除classloader
         *
         * @param loader
         * @return
         */
        @SneakyThrows
        public ClassLoaderWrapper remove(ClassLoader loader) {
            Stack<ClassLoader> cls = new Stack<>();
            ClassLoader cl = this;
            while (cl instanceof ClassLoaderWrapper) {
                ClassLoader origin = ((ClassLoaderWrapper) cl).unwrap();
                if (!origin.equals(loader)) {
                    cls.push(origin);
                }
                cl = cl.getParent();
            }
            if (cl != null && !cl.equals(loader)) {
                cls.push(cl);
            }
            //here parent loader must be not null
            ClassLoader parent = cls.pop();
            while (!cls.empty()) {
                parent = new ClassLoaderWrapper(cls.pop(), parent);
            }
            return parent instanceof ClassLoaderWrapper ? (ClassLoaderWrapper) parent : new ClassLoaderWrapper(parent, null);
        }

        /**
         * 查找指定的classloader
         *
         * @param loader
         * @return
         */
        public ClassLoader find(ClassLoader loader) {
            ClassLoader cl = this.classLoader;
            while (cl instanceof ClassLoaderWrapper) {
                ClassLoader origin = ((ClassLoaderWrapper) cl).unwrap();
                if (origin.equals(loader)) {
                    return origin;
                }
                cl = cl.getParent();
            }
            if (cl != null && cl.equals(loader)) {
                return cl;
            }
            return null;
        }

        public ClassLoader unwrap() {
            if (this.classLoader instanceof ClassLoaderWrapper) {
                return ((ClassLoaderWrapper) this.classLoader).unwrap();
            }
            return this.classLoader;
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
                    if (c == null && this.classLoader != null) {
                        c = this.classLoader.loadClass(name);
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
            return classLoader.getResources(name);
        }

        @Override
        public URL findResource(String name) {
            return classLoader.getResource(name);
        }

    }


}

