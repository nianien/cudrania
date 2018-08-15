package com.cudrania.test;

import com.cudrania.spring.SpringLauncher;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.fail;

/**
 * @author scorpio
 * @version 1.0.0
 */
public class TestMain {

    private ApplicationContext context;

    @Before
    public void setup() {
        System.setProperty("exp1", "true");
        System.setProperty("exp2", "true");
        System.setProperty("spring.profiles.active", "~task,all");
        context = SpringLauncher.asContext(TestBean.class);
    }


    @Test//(expected = NoSuchBeanDefinitionException.class)
    public void testDoService() {
        SpringLauncher.asBean(TestBean.class).doService();
        context.getBean(TestBean.class).doService();
        SpringLauncher.run(TestBean.class, "doService");
    }

    @Test//(expected = NoSuchBeanDefinitionException.class)
    public void testHasBean() {
        System.out.println(context.getBean(BeanOnExpression.class));
        System.out.println(context.getBean(BeanOnProperty.class));
        System.out.println(context.getBean(BeanOnProperties.class));
        try {
            System.out.println(context.getBean(BeanOnProfile.class));
        } catch (BeansException e) {
            e.printStackTrace();
            fail("Exception should not be thrown");
        }

    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void testHasNoBean() {
        System.out.println(context.getBean(BeanOnProfile.class));
    }
}
