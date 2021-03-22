package com.cudrania.core.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示已标记
 *
 * @author skyfalling
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Marked {
    /**
     * 标记
     *
     * @return
     */
    String value() default "";
}
