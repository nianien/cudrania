package com.cudrania.test;

import com.cudrania.spring.ApplicationLoader;

import org.springframework.context.ApplicationContext;

/**
 * @author scorpio
 * @version 1.0.0
 */
public class BeanTest {

  public static void main(String[] args) {


    System.setProperty("exp1","true");
    System.setProperty("exp2","true");
    ApplicationLoader.get(TestBean.class).doService();
    ApplicationContext context = ApplicationLoader.loadContext(TestBean.class);
    context.getBean(TestBean.class).doService();
    ApplicationLoader.run(TestBean.class,"doService");
    System.out.println(context.getBean(BeanOnExpression.class));
    System.out.println(context.getBean(BeanOnProperty.class));
    System.out.println(context.getBean(BeanOnProperties.class));
  }
}
