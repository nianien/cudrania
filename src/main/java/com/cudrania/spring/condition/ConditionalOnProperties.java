package com.cudrania.spring.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Conditional} that checks if the specified properties matches in logic combination.
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
   * 条件组合
   *
   * @return
   */
  ConditionalOnProperty[] value();

  /**
   * 逻辑组合
   *
   * @return
   */
  Logic logic() default Logic.AND;

  /**
   * 逻辑判断
   */
  enum Logic {

    AND, OR
  }
}
