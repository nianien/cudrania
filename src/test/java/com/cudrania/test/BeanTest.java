package com.cudrania.test;

import com.cudrania.spring.beans.ApplicationBean;

import org.springframework.context.ApplicationContext;

/**
 * @author scorpio
 * @version 1.0.0
 */
public class BeanTest {

  public static void main(String[] args) {


    System.setProperty("exp1","true");
    System.setProperty("exp2","true");
    ApplicationContext context = ApplicationBean.loadContext(TestBean.class);
    System.out.println(context.getBeansOfType(ApplicationBean.class));
    context.getBean(TestBean.class).doService();
    new TestBean().init().doService();
    System.out.println(context.getBean(BeanOnExpression.class));
    System.out.println(context.getBean(BeanOnProperty.class));
    System.out.println(context.getBean(BeanOnProperties.class));
  }
}
