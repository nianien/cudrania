package com.cudrania.core.proxy;

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
            Object result;
            // 代理前的处理
            interceptor.before(target1, method, args);
            try {
                // 执行被代理的方法
                result = method.invoke(target1, args);
            } catch (Exception ex) {
                result = interceptor.exception(ex, target1, method, args);
            }
            // 代理之后的处理
            interceptor.after(target1, method, args);
            return result;
        });
    }

}
