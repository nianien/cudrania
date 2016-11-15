package com.cudrania.spring.beans;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Spring上下文的加载类
 *
 * @author skyfalling
 * @date 16/11/15
 */
public class BeanLoader {
    /**
     * 加载Spring托管对象
     *
     * @param targetClass
     * @param <T>
     * @return
     */
    public final static <T> T loadBean(Class<T> targetClass) {
        ApplicationContext context = loadContext(targetClass);
        return context.getBean(targetClass);
    }

    /**
     * 加载Spring托管对象
     *
     * @param targetClass
     * @return
     */
    public final static ApplicationContext loadContext(Class<?> targetClass) {
        return loadContext(targetClass, Object.class, Object.class);
    }


    /**
     * 加载基于注解的{@link ApplicationContext}对象
     *
     * @param targetClass    加载目标类
     * @param topClass       目标类的基类
     * @param excludeClasses 排除加载的类
     * @return
     */
    public final static ApplicationContext loadContext(Class<?> targetClass, Class topClass, Class... excludeClasses) {
        Set<Class> excludeSet = new HashSet<>(Arrays.asList(excludeClasses));
        List<Class> classes = new ArrayList<>();
        Class curClass = targetClass;
        while (topClass.isAssignableFrom(targetClass)) {
            if (!excludeSet.contains(curClass)) {
                classes.add(curClass);
            }
            curClass = curClass.getSuperclass();
        }
        return new AnnotationConfigApplicationContext(classes.toArray(new Class[0]));
    }

    /**
     * 加载基于配置文件的{@link ApplicationContext}对象
     *
     * @param resources 配置资源
     * @return
     */
    public final static ApplicationContext loadContext(String[] resources) {
        return new ClassPathXmlApplicationContext(resources);
    }

}
