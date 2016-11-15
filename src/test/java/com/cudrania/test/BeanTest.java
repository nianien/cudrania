package com.cudrania.test;

import com.cudrania.spring.beans.ContextBean;

import org.springframework.context.ApplicationContext;

/**
 * @author scorpio
 * @version 1.0.0
 */
public class BeanTest {

  public static void main(String[] args) {


    ApplicationContext context = ContextBean.loadContext(TestBean.class);
    System.out.println(context.getBeansOfType(ContextBean.class));
    context.getBean(TestBean.class).doService();
    new TestBean().init().doService();
  }
}
