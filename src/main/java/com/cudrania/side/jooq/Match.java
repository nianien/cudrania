package com.cudrania.side.jooq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于字段匹配的注解
 * scm.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Match {
    /**
     * SQL操作符
     *
     * @return
     */
    Operator op() default Operator.EQ;

    /**
     * 用于匹配的字段名
     *
     * @return
     */
    String name() default "";

    /**
     * 是否停用
     *
     * @return
     */
    boolean disable() default false;
}
