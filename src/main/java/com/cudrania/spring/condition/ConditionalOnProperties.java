package com.cudrania.spring.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Conditional} that checks if the specified properties matches in logic combination<br/>
 * when {@link #value()} is empty, the {@link Conditional} is not matched.
 *
 * @author scorpio
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnPropertiesCondition.class)
public @interface ConditionalOnProperties {
    /**
     * combination {@link ConditionalOnProperty}s with {@link Operator}
     *
     * @return
     */
    ConditionalOnProperty[] value();

    /**
     * Logic operator
     *
     * @return
     */
    Operator operator() default Operator.AND;

}
