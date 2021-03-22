package com.cudrania.core.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示已注册
 *
 * @author skyfalling
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Registered {
}
