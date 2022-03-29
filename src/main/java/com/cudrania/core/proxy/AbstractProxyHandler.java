package com.cudrania.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理对象的处理类<br>
 * 根据拦截器对被代理的方法进行拦截处理
 *
 * @author skyfalling
 */
public abstract class AbstractProxyHandler implements InvocationHandler {

    /**
     * 被代理对象实例
     */
    private Object target;


    /**
     * 构造函数
     */
    public AbstractProxyHandler() {
    }

    /**
     * 构造函数,指定被代理对象实例
     *
     * @param target 被代理对象实例
     */
    public AbstractProxyHandler(Object target) {
        this.target = target;
    }

    /**
     * 获取被代理对象实例
     *
     * @return
     */
    public Object getTarget() {
        return target;
    }

    /**
     * 设置被代理对象实例
     *
     * @param target
     */
    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * 执行代理方法
     *
     * @param target 被代理的实例
     * @param method 被代理的方法
     * @param args   方法参数
     * @return
     */
    public abstract Object proxy(Object target, Method method, Object... args);

    /**
     * 重写代理方法,这里的proxy是生成的代理实例,重写方法替换成被代理的实例
     */
    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) {
        return this.proxy(this.target, method, args);
    }

}
