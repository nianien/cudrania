package com.cudrania.test;

import com.cudrania.spring.SpringLauncher;

import org.springframework.context.ApplicationContext;

/**
 * @author scorpio
 * @version 1.0.0
 */
public class TestMain {

    public static void main(String[] args) {


        System.setProperty("exp1", "true");
        System.setProperty("exp2", "true");
        System.setProperty("spring.profiles.active", "~task");
        SpringLauncher.asBean(TestBean.class).doService();
        ApplicationContext context = SpringLauncher.asContext(TestBean.class);
        context.getBean(TestBean.class).doService();
        SpringLauncher.run(TestBean.class, "doService");
        System.out.println(context.getBean(BeanOnExpression.class));
        System.out.println(context.getBean(BeanOnProperty.class));
        System.out.println(context.getBean(BeanOnProperties.class));
        System.out.println(context.getBean(BeanOnProfile.class));
    }
}
