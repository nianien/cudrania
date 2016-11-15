package com.cudrania.spring.beans;

import org.springframework.context.ApplicationContext;


/**
 * Spring托管对象，通过{@link #init()}方法获取Spring托管对象,通过{@link org.springframework.context.annotation.Import}和{@link
 * org.springframework.context.annotation.ImportResource}注解指定Spring配置文件<br/> for example:
 * <pre>
 * &#064;ImportResource("classpath:spring-root.xml")
 * public class MyBean {
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
 * @date 16/11/15
 */
public class SpringBean<T extends SpringBean> {

    private ApplicationContext context;

    /**
     * 返回Spring托管对象
     */
    public synchronized final T init() {
        if (context == null) {
            context = BeanLoader.loadContext(this.getClass(), SpringBean.class, SpringBean.class);
        }
        return (T) context.getBean(this.getClass());
    }


    /**
     * 获取Spring上下文
     *
     * @return
     */
    public ApplicationContext getContext() {
        return context;
    }


}
