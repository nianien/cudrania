package com.cudrania.core.loader;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Stack;

/**
 * 支持多文件类加载器，可以动态增删文件资源
 * Created on 2022/10/21
 *
 * @author liyifei
 */
public class FilesClassLoader extends ClassLoader {

    private ClassLoaderWrapper classLoader;

    public FilesClassLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public FilesClassLoader(ClassLoader classLoader) {
        assert classLoader != null;
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
     * 添加jar文件
     *
     * @param file
     * @return
     */
    public FilesClassLoader add(String file) {
        return add(new File(file));
    }

    /**
     * 添加jar文件
     *
     * @param file
     * @return
     */
    public FilesClassLoader add(File file) {
        this.classLoader = classLoader.add(new FileClassLoader(file));
        return this;
    }

    /**
     * 移除jar文件
     *
     * @param file
     * @return
     */
    @SneakyThrows
    public FilesClassLoader remove(String file) {
        return remove(new File(file));
    }

    /**
     * 移除jar文件
     *
     * @param file
     * @return
     */
    @SneakyThrows
    public FilesClassLoader remove(File file) {
        this.classLoader = classLoader.remove(new FileClassLoader(file));
        return this;
    }


    /**
     * 获取指定文件的classloader
     *
     * @param file
     * @return
     */
    public FileClassLoader get(File file) {
        ClassLoader cl = this.classLoader;
        while (cl instanceof ClassLoaderWrapper) {
            cl = ((ClassLoaderWrapper) cl).unwrap();
            if (cl instanceof FileClassLoader) {
                FileClassLoader fcl = (FileClassLoader) cl;
                if (fcl.getFile().equals(file)) {
                    return fcl;
                }
            }
            cl = cl.getParent();
        }
        if (cl instanceof FileClassLoader) {
            FileClassLoader fcl = (FileClassLoader) cl;
            if (fcl.getFile().equals(file)) {
                return fcl;
            }
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
                cl = ((ClassLoaderWrapper) cl).unwrap();
                if (cl.equals(loader)) {
                    cl = cl.getParent();
                    continue;
                }
                cls.push(cl);
                cl = cl.getParent();
            }
            if (cl != null) {
                cls.push(cl);
            }
            //here parent loader must be not null
            ClassLoader parent = cls.pop();
            while (!cls.empty()) {
                parent = new ClassLoaderWrapper(cls.pop(), parent);
            }
            return new ClassLoaderWrapper(parent, null);
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

