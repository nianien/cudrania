package com.cudrania.spring.beans;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * Spring托管对象,通过{@link org.springframework.context.annotation.Import}和{@link
 * org.springframework.context.annotation.ImportResource}注解指定Spring配置文件<br/>
 * for example:
 * <pre>
 * &#064;ImportResource("classpath:spring-root.xml")
 * public class MyBean extends ContextBean&lt;MyBean> {
 *
 *    &#064;AutoWired
 *    private Service service;
 *
 *    public void doSomething(){
 *       service.doSomething();
 *    }
 *
 *    public void doOtherThing(int a,String b,...){
 *       service.doSomething();
 *    }
 *  }
 * </pre>
 * <code> 使用方式: new MyBean().init().doSomething() new MyBean().init().doOtherThing(a,b) </code>
 *
 * @author skyfalling
 * @version 1.0.0
 */
public class ContextBean<T extends ContextBean> {


    private ApplicationContext context;


    /**
     * 初始化Spring配置
     *
     * @return Spring托管对象, 不是当前对象
     */
    public final T init() {
        return (T) getContext().getBean(this.getClass());
    }

    /**
     * 获取Spring上下文
     *
     * @return
     */
    public ApplicationContext getContext() {
        if (context == null) {
            synchronized (this) {
                this.context = loadContext(this.getClass());
            }
        }
        return this.context;
    }


    /**
     * 根据类定义加载{@link ApplicationContext}对象<br/>
     *
     * @return
     */
    public static ApplicationContext loadContext(Class<?>... classes) {
        return new AnnotationConfigApplicationContext(classes);
    }

}
