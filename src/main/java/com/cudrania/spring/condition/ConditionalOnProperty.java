package com.cudrania.spring.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基于系统属性变量的{@link Conditional}实现
 *
 * @author scorpio
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(ConditionOnProperty.class)
public @interface ConditionalOnProperty {

  /**
   * 属性名称
   *
   * @return
   */
  String name();

  /**
   * 属性值
   *
   * @return
   */
  String value();

  /**
   * 匹配类型
   *
   * @return
   */
  Matches match() default Matches.EQUALS;

  /**
   * 是否取反
   *
   * @return
   */
  boolean inverse() default false;

  /**
   * 是否忽略大小写
   *
   * @return
   */
  boolean caseIgnore() default false;

  /**
   * 匹配模式
   */
  enum Matches {
    EQUALS, CONTAINS, WILDCARD, REGEX
  }
}
