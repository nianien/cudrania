package com.cudrania.core.proxy;

import java.lang.reflect.Method;

/**
 * 拦截器的接口声明
 *
 * @author skyfalling
 *         拦截对象的类型
 */
public interface Interceptor {

    /**
     * 拦截前处理
     *
     * @param target 被代理的实例
     * @param method 被代理的方法
     * @param args   被代理方法的参数
     */
    void before(Object target, Method method, Object... args);

    /**
     * 拦截后处理
     *
     * @param target 被代理的实例
     * @param method 被代理的方法
     * @param args   被代理方法的参数
     */
    void after(Object target, Method method, Object... args);

    /**
     * 异常处理,当方法执行出现异常时,异常处理的结果将被返回
     *
     * @param ex     异常实例
     * @param target 被代理的实例
     * @param method 被代理的方法
     * @param args   被代理方法的参数
     * @return 异常时的返回结果
     */
    Object exception(Exception ex, Object target, Method method, Object... args);
}
