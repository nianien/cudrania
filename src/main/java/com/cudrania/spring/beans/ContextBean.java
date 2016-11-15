package com.cudrania.spring.beans;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


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
        this.context = loadContext(this.getClass(), ContextBean.class, ContextBean.class);
      }
    }
    return this.context;
  }

  /**
   * 根据类定义加载{@link ApplicationContext}对象<br/>
   * 等价于 loadContext(clazz, clazz)
   *
   * @param clazz 配置类
   * @return
   * @see #loadContext(Class, Class, Class[])
   */
  public static ApplicationContext loadContext(Class<?> clazz) {
    return loadContext(clazz, clazz, clazz);
  }


  /**
   * 根据类定义加载{@link ApplicationContext}对象<br/>
   * 默认会加载继承类
   *
   * @param clazz          配置类
   * @param limitClass     继承类的上限
   * @param excludeClasses 排除加载的类
   * @return
   */
  public static ApplicationContext loadContext(Class<?> clazz, Class limitClass, Class... excludeClasses) {
    Set<Class> includes = new LinkedHashSet<>();
    Set<Class> excludes = new HashSet<>(Arrays.asList(excludeClasses));
    includes.add(clazz);
    excludes.add(Object.class);
    Class curClass = clazz;
    while (curClass != null && limitClass.isAssignableFrom(curClass)) {
      if (!excludes.contains(curClass)) {
        includes.add(curClass);
      }
      curClass = curClass.getSuperclass();
    }
    return new AnnotationConfigApplicationContext(includes.toArray(new Class[0]));
  }

}
