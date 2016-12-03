package com.cudrania.test;

import com.cudrania.spring.beans.ApplicationBean;

import org.springframework.context.ApplicationContext;

/**
 * @author scorpio
 * @version 1.0.0
 */
public class BeanTest {

  public static void main(String[] args) {


    ApplicationContext context = ApplicationBean.loadContext(TestBean.class);
    System.out.println(context.getBeansOfType(ApplicationBean.class));
    context.getBean(TestBean.class).doService();
    new TestBean().init().doService();
  }
}
