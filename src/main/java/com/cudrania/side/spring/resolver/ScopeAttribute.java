package com.cudrania.side.spring.resolver;

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
    Scope[] scope() default {Scope.THREAD, Scope.REQUEST, Scope.SESSION};

    /**
     * 定义数据的生命周期
     *
     * @author skyfalling
     */
    enum Scope {
        THREAD, REQUEST, SESSION;
    }
}
