package com.cudrania.core.proxy;

import com.cudrania.core.exception.ExceptionChecker;

import java.lang.reflect.Method;
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
    public static Object proxy(Object target, ProxyHandler handler) {
        try {
            handler.setTarget(target);
            return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
                    handler);
        } catch (Exception e) {
            return ExceptionChecker.throwException(e);
        }
    }

    /**
     * 获取被代理对象的代理实例
     *
     * @param target      被代理对象
     * @param interceptor 拦截器实例
     * @return 代理实例
     */
    public static Object proxy(Object target, final Interceptor interceptor) {

        return interceptor == null ? target : Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
                new ProxyHandler(target) {

                    @Override
                    public Object proxy(Object target, Method method, Object... args) {
                        Object result;
                        // 代理前的处理
                        interceptor.before(target, method, args);
                        try {
                            // 执行被代理的方法
                            result = method.invoke(target, args);
                        } catch (Exception ex) {
                            result = interceptor.exception(ex, target, method, args);
                        }
                        // 代理之后的处理
                        interceptor.after(target, method, args);
                        return result;
                    }

                });
    }

}
