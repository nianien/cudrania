package com.cudrania.spring.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Conditional} that checks if the specified properties matches in logic combination<br/>
 * when {@link #value()} is empty,
 * this {@link Conditional} is matched if conj is {@link LogicalConj#AND},
 * and not matched if conj is {@link LogicalConj#OR},
 *
 * @author scorpio
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(ConditionOnProperties.class)
public @interface ConditionalOnProperties {
  /**
   * combination {@link ConditionalOnProperty}s with {@link com.cudrania.spring.condition.ConditionalOnProperties.LogicalConj}
   *
   * @return
   */
  ConditionalOnProperty[] value();

  /**
   * conj of Logical Connectives
   *
   * @return
   */
  LogicalConj conj() default LogicalConj.AND;

  /**
   * Logical Connectives
   */
  enum LogicalConj {

    AND, OR
  }
}
