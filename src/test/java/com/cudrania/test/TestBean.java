package com.cudrania.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author scorpio
 * @version 1.0.0
 */
@ComponentScan
//@Import(ApplicationBean.class)
public class TestBean {


  @Autowired
  private TestService testService;


  public void doService() {
    testService.test();
  }
}
