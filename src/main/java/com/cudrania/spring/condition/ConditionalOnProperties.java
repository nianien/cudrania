package com.cudrania.spring.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性条件判断
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
  Logical conjunction() default Logical.AND;

  /**
   * 逻辑判断
   */
  enum Logical {

    AND, OR
  }
}
