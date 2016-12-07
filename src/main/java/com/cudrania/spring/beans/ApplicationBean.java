package com.cudrania.spring.beans;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * Spring托管对象,通过{@link org.springframework.context.annotation.Import}和{@link
 * org.springframework.context.annotation.ImportResource}注解指定Spring配置文件<br/>
 * for example:
 * <pre>
 * &#064;ImportResource("classpath:spring-root.xml")
 * public class MyBean extends ApplicationBean&lt;MyBean> {
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
public class ApplicationBean<T extends ApplicationBean> {


  protected ApplicationContext context;


  /**
   * 初始化托管对象
   *
   * @return 当前类对象的托管对象
   */
  public final T init() {
    T bean = (T) getContext().getBean(this.getClass());
    bean.context = context;
    return bean;
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
