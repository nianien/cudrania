package com.cudrania.core.reflection;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定属性别名的注解<br>
 * 一般在对Getter或Setter方法不使用默认属性规则时使用该注解
 *
 * @author skyfalling
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Property {
    /**
     * 属性名称
     *
     * @return
     */
    String value();
}
