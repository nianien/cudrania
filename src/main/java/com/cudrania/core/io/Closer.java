package com.cudrania.core.io;

import com.cudrania.core.reflection.Reflections;

/**
 * 执行对象声明的close方法
 *
 * @author skyfalling
 */
public class Closer {

    /**
     * 关闭closeable对象<br>
     *
     * @param closeable
     */
    public static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }


    /**
     * 如果对象声明了close方法,则执行该方法<br> 该方法不会发生异常
     *
     * @param object
     */
    public static void close(Object object) {
        if (object != null) {
            try {
                if (object instanceof AutoCloseable) {
                    ((AutoCloseable) object).close();
                } else {
                    Reflections.invoke("close", object, new Object[0]);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 依次关闭指定的对象
     *
     * @param object
     */
    public static void close(Object... object) {
        for (Object obj : object) {
            close(obj);
        }
    }
}
