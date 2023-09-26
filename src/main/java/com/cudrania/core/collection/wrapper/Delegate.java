package com.cudrania.core.collection.wrapper;

import java.lang.annotation.*;

/**
 * 用于代理方法声明，被该注解声明的方法，在执行时会自动调用注解指定的代理方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Delegate {

    /**
     * 代理方法名称
     *
     * @return
     */
    String value() default "";

}
