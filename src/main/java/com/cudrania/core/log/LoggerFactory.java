package com.cudrania.core.log;

import com.cudrania.core.loader.ResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * {@link java.util.logging.Logger}对象的工厂类,修改{@link LogManager}对象读取日志配置文件的顺序<p>
 * <ol>
 * <li>系统属性(system property):java.util.logging.config.file</li>
 * <li>当前路径下的log.properties文件</li>
 * <li>classpath中的log.properties文件</li>
 * <li>系统默认配置</li>
 * </ol>
 *
 * @author skyfalling
 */
public class LoggerFactory {
    static {
        //jul默认支持的属性配置
        if (System.getProperty("java.util.logging.config.file") == null) {
            load("logging.properties");
        }
    }

    /**
     * 根日志对象
     */
    private static Logger root = Logger.getLogger("");

    /**
     * 设置全局日志级别
     *
     * @param level
     */
    public static void setLevel(Level level) {
        root.setLevel(level);
        for (Handler handler : root.getHandlers()) {
            handler.setLevel(level);
        }
    }

    /**
     * 获取Logger对象
     *
     * @param clazz
     * @return
     */
    public static Logger getLogger(Class clazz) {
        return Logger.getLogger(clazz.getName());
    }

    /**
     * 获取Logger对象
     *
     * @param namespace
     * @return
     */
    public static Logger getLogger(String namespace) {
        return Logger.getLogger(namespace);
    }

    /**
     * 获取Logger对象,{@link Logger#log(java.util.logging.Level, String)}方法中的msg参数为资源文件的键值
     *
     * @param clazz
     * @param resourceBundleName
     * @return
     */
    public static Logger getLogger(Class clazz, String resourceBundleName) {
        return Logger.getLogger(clazz.getName(), resourceBundleName);
    }

    /**
     * 获取Logger对象,{@link Logger#log(java.util.logging.Level, String)}方法中的msg参数为资源文件的键值
     *
     * @param namespace
     * @param resourceBundleName
     * @return
     */
    public static Logger getLogger(String namespace, String resourceBundleName) {
        return Logger.getLogger(namespace, resourceBundleName);
    }


    /**
     * 加载日志配置文件
     *
     * @param path
     */
    private static void load(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file = ResourceLoader.getFile(path);
            }
            if (file != null && file.exists()) {
                LogManager.getLogManager().readConfiguration(new FileInputStream(file));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
