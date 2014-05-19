package com.cudrania.spring.resolver;

import java.lang.annotation.*;

/**
 * 用于绑定Session或Request属性值的注解
 *
 * @author skyfalling
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScopeAttribute {

    /**
     * 绑定数据的名称
     *
     * @return
     */
    String value() default "";

    /**
     * 绑定数据的生命周期
     *
     * @return
     */
    Scope scope() default Scope.DEFAULT;

    /**
     * 定义数据的生命周期
     *
     * @author skyfalling
     */
    public enum Scope {
        REQUEST, SESSION, THREAD, DEFAULT;
    }
}
