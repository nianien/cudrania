package com.cudrania.core.proxy;

import java.lang.reflect.Method;

/**
 * 动态代理对象的处理类<br>
 * 根据拦截器对被代理的方法进行拦截处理
 *
 * @author skyfalling
 */
public interface ProxyHandler<T> {

    /**
     * 执行代理方法
     *
     * @param proxy  代理实例
     * @param target 被代理实例
     * @param method 被代理方法
     * @param args   方法参数
     * @return
     */
    Object proxy(Object proxy, T target, Method method, Object[] args);
}
