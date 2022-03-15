package com.cudrania.core.reflection;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于是否忽略
 *
 * @author skyfalling
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Ignore {
    /**
     * 是否忽略
     *
     * @return
     */
    boolean value() default true;
}
