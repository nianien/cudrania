package com.cudrania.spring;

import com.nianien.core.reflect.Reflections;
import com.nianien.core.util.StringUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

/**
 * 基于类定义的注解加载Spring上下文，执行Bean对象指定的方法<br/>
 * for example:
 * <pre>
 * package com.my.bean
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
 *       service.doOtherThing(a,b);
 *    }
 *  }
 * </pre>
 * 调用命令如下:
 * <ol>
 * <li>java com.cudrania.spring.AppRunner com.my.bean.MyBean#doSomething</li>
 * <li>java com.cudrania.spring.AppRunner com.my.bean.MyBean#doOtherThing 1 test</li>
 * <li>java com.cudrania.spring.AppRunner com.my.bean.MyBean doOtherThing 1 test</li>
 * </ol>
 *
 * @author skyfalling
 */
public class AppRunner {


  /**
   * 初始化托管对象
   *
   * @return 当前类的托管对象
   */
  public static final <T> T get(Class<T> beanClass) {
    ApplicationContext context = loadContext(beanClass);
    T t = context.getBean(beanClass);
    if (t instanceof ApplicationContextAware) {
      ((ApplicationContextAware) t).setApplicationContext(context);
    }
    return t;
  }


  /**
   * 根据类定义加载{@link ApplicationContext}对象<br/>
   *
   * @return
   */
  public static ApplicationContext loadContext(Class<?>... classes) {
    return new AnnotationConfigApplicationContext(classes);
  }


  /**
   * 执行bean方法
   *
   * @param beanClass
   * @param method
   * @param args
   * @throws Exception
   */
  public static void run(String beanClass, String method, Object... args) throws Exception {
    Class<?> clazz = Class.forName(beanClass);
    Object bean = loadContext(clazz);
    Reflections.invoke(method, bean, args);
  }

  /**
   * bean对象方法执行入口
   *
   * @param args beanClass#methodName [args...]
   *             beanClass methodName [args...]
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    String beanClass = StringUtils.substringBeforeLast(args[0], "#");
    String method = StringUtils.substringAfterLast(args[0], "#");
    String[] params;
    if (StringUtils.isEmpty(method)) {
      method = args[1];
      params = Arrays.copyOfRange(args, 2, args.length);
    } else {
      params = Arrays.copyOfRange(args, 1, args.length);
    }
    run(beanClass, method, params);
  }


}
