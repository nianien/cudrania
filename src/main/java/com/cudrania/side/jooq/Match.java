package com.cudrania.side.jooq;

import java.lang.annotation.*;

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
    Operator value() default Operator.EQ;

    /**
     * 用于匹配的字段名
     *
     * @return
     */
    String name() default "";

}
