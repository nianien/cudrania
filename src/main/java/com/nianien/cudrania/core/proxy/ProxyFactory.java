package com.nianien.cudrania.core.proxy;

import lombok.SneakyThrows;

import java.lang.reflect.Proxy;

/**
 * 动态代理工厂类,用来创建代理实例
 *
 * @author skyfalling
 */
public class ProxyFactory {
    /**
     * 获取被代理对象的代理实例
     *
     * @param target  被代理对象
     * @param handler ProxyHandler对象
     * @return 代理实例
     */
    @SneakyThrows
    public static <T> Object proxy(T target, ProxyHandler<T> handler) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
                (proxy, method, args) -> handler.proxy(proxy, target, method, args));
    }

    /**
     * 获取被代理对象的代理实例
     *
     * @param target      被代理对象
     * @param interceptor 拦截器实例
     * @return 代理实例
     */
    public static Object proxy(Object target, final Interceptor interceptor) {

        if (interceptor == null) {
            return target;
        }
        return proxy(target, (proxy, target1, method, args) -> {
            Object result = null;
            // 代理前的处理
            interceptor.before(target1, method, args);
            try {
                try {
                    // 执行被代理的方法
                    result = method.invoke(target1, args);
                } catch (Exception ex) {
                    //method.invoke会把业务方法抛出的异常包装成InvocationTargetException, 解包出真实异常再交给拦截器
                    Throwable cause = ex instanceof java.lang.reflect.InvocationTargetException
                            ? ((java.lang.reflect.InvocationTargetException) ex).getTargetException() : ex;
                    Exception real = cause instanceof Exception ? (Exception) cause : ex;
                    result = interceptor.exception(real, target1, method, args);
                }
                return result;
            } finally {
                // 代理之后的处理: 放在finally, 保证异常路径(exception()重抛)也会执行after
                interceptor.after(target1, method, args);
            }
        });
    }

}
