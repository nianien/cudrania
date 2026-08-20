package com.nianien.cudrania.core.loader;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 多个类加载器聚合的classloader，支持运行时动态增删类加载器。
 * <p>
 * <b>解决的问题</b>：在保持标准"双亲委派"语义的前提下，动态地为应用扩展类路径
 * （典型场景：插件/扩展、运行时加载外部jar或目录，见{@link FileClassLoader}）。
 * <p>
 * <b>解析顺序（parent-first，刻意如此）</b>：{@link #wrapped()}把
 * {@code rootLoader}与后续加入的loader包装成一条parent链（见{@link ClassLoaderWrapper#of}），
 * 由于{@link ClassLoaderWrapper}沿用JDK标准的{@code loadClass}（双亲优先）算法，
 * 一次加载的实际尝试顺序为：
 * <pre>
 *   bootstrap → rootLoader → 先加入的loader → … → 后加入的loader
 * </pre>
 * 其中{@link #insert}插入队首（优先级更高），{@link #add}追加队尾（优先级更低），但二者<b>都低于rootLoader</b>。
 * <p>
 * <b>为什么rootLoader优先级最高（而不是让新加入的loader覆盖）</b>：应用/JDK核心类、
 * 以及跨插件<b>共享的接口/SPI契约类</b>必须由rootLoader加载成<b>同一份</b>；附加loader只提供
 * rootLoader加载不到的<b>新</b>类。若反过来做成child-first让插件覆盖应用已有的类，同一类会被
 * 不同loader各加载一份，引发{@code ClassCastException}/{@code LinkageError}/类身份冲突——
 * 本类的委派设计正是为规避该问题。
 * <p>
 * <b>动态生效</b>：{@code classLoaders}为可变列表，{@link #wrapped()}每次按当前列表重建委派链，
 * 因此{@code add/insert/remove}对下一次加载立即生效，无需重建本对象；由于
 * {@link ClassLoaderWrapper}是"非定义型"包装器（自身从不{@code defineClass}，仅路由到真实loader），
 * 反复重建包装链没有副作用，类的定义与缓存始终由底层真实loader持有。
 * <p>
 * <b>资源查找（Spring配置）</b>：{@link #getResource}/{@link #getResources}只委托给最外层loader，
 * 而<b>不</b>沿wrapper链聚合。因为附加loader({@link FileClassLoader})是child-first且父加载器为
 * system/context loader，已能通过自身父委托摸到rootLoader(应用classpath)的资源；若再逐层聚合，
 * rootLoader的同一份资源会被重复枚举，导致Spring按{@code classpath*:}扫描时出现重复配置。
 * 只查最外层即保证"各资源恰好一次"（对应{@code classpath:}取首个、{@code classpath*:}枚举全部且不重复）。
 * <p>
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

    /**
     * 加载类：委托给按当前loader列表实时构建的委派链，
     * 解析顺序为 bootstrap → rootLoader → 先加入 → … → 后加入（见类注释）。
     */
    public Class<?> loadClass(String name)
            throws ClassNotFoundException {
        return wrapped().loadClass(name);
    }


    /**
     * 查找资源枚举，供Spring按{@code classpath*:}扫描配置等场景使用。
     * <p>
     * 委托给最外层loader（见{@link ClassLoaderWrapper#getResources}）：靠附加loader自身的
     * child-first父委托摸到rootLoader资源，从而<b>各资源恰好枚举一次、不重复</b>。
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return wrapped().getResources(name);
    }

    /**
     * 查找单个资源，供Spring按{@code classpath:}查找配置等场景使用。
     * <p>
     * 委托给最外层loader：附加loader child-first先查自身、找不到再委托父链(可达rootLoader)。
     */
    @Override
    public URL getResource(String name) {
        return wrapped().getResource(name);
    }


    /**
     * 追加类加载器到队尾（优先级最低，仍低于rootLoader）。
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
     * 插入类加载器到队首（在所有附加loader中优先级最高，但仍低于rootLoader）。
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
     * 按当前rootLoader与loader列表<b>实时构建</b>委派链并返回其最外层包装器。
     * 每次调用都重建，从而让{@code add/insert/remove}立即生效。
     *
     * @return 委派链最外层的包装器
     */
    public ClassLoaderWrapper wrapped() {
        return ClassLoaderWrapper.of(rootLoader, classLoaders.toArray(new ClassLoader[0]));
    }


    /**
     * 类加载器包装类：把一个真实ClassLoader接入JDK标准的双亲委派链。
     * <p>
     * 关键点在于<b>只重写{@link #findClass}（转交给被包装的真实loader），而不重写{@code loadClass}</b>，
     * 从而复用{@link ClassLoader#loadClass}的parent-first算法：先向parent（链上更内层的包装器）委派，
     * 失败后才用本层的真实loader加载。多个包装器串成parent链即实现"按序回退"的复合加载。
     * <p>
     * 本包装器是"非定义型"的——自身从不{@code defineClass}，因此不持有类定义/缓存，
     * 可被{@link #wrapped()}反复重建而无副作用。
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



        /**
         * 仅当parent链均无法加载时才被调用（parent-first），用本层真实loader加载。
         */
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return this.classloader.loadClass(name);
        }

        /**
         * 资源查找<b>只委托给最外层的真实loader</b>，而<b>不</b>沿wrapper的parent链聚合。
         * <p>
         * 这是刻意为之：附加loader({@link FileClassLoader})本身是child-first且父加载器为
         * system/context loader，其{@code getResources}会在自身找不到时委托到父链，从而已能摸到
         * rootLoader(应用classpath)的资源。若此处再沿wrapper链逐层聚合，rootLoader的同一份资源
         * 会经"rootLoader包装器"和"各附加loader的真实父链"被<b>重复枚举</b>，导致Spring按
         * {@code classpath*:}扫描时出现重复配置/重复bean定义。只查最外层即保证"各资源恰好一次"。
         */
        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return classloader.getResources(name);
        }

        @Override
        public URL getResource(String name) {
            return classloader.getResource(name);
        }


        /**
         * 构建委派链：rootLoader为最内层（parent=null，其后即bootstrap），
         * 依次把每个loader包装为更外层，parent指向前一层。返回最外层包装器。
         * <p>
         * 于是从最外层触发的parent-first解析顺序为：
         * bootstrap → rootLoader → classLoaders[0] → … → classLoaders[n-1]。
         *
         * @param rootLoader   默认（最高优先级）类加载器
         * @param classLoaders 附加类加载器，数组顺序即优先级（靠前者优先）
         * @return 委派链最外层的包装器
         */
        private static ClassLoaderWrapper of(ClassLoader rootLoader, ClassLoader... classLoaders) {
            ClassLoaderWrapper wrapper = new ClassLoaderWrapper(rootLoader, null);
            for (ClassLoader classLoader : classLoaders) {
                wrapper = new ClassLoaderWrapper(classLoader, wrapper);
            }
            return wrapper;
        }

    }


}

