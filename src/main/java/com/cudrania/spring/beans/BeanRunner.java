package com.cudrania.spring.beans;

import com.nianien.core.reflect.Reflections;

import org.apache.commons.lang3.StringUtils;

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
 *       service.doSomething();
 *    }
 *  }
 * </pre>
 * 调用命令如下:
 * <ol>
 * <li>java com.cudrania.spring.beans.BeanRunner com.my.bean.MyBean#doSomething</li>
 * <li>java com.cudrania.spring.beans.BeanRunner com.my.bean.MyBean#doOtherThing 1 test</li>
 * </ol>
 *
 * @author skyfalling
 * @see ContextBean
 */
public class BeanRunner {

  /**
   * bean对象方法执行入口
   *
   * @param args beanClass#methodName [args...]
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
    Class<?> clazz = Class.forName(beanClass);
    Object bean = ContextBean.loadContext(clazz).getBean(clazz);
    Reflections.invoke(method, bean, params);
  }


}
