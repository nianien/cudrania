package com.cudrania.spring.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * {@link Conditional} that checks if the specified property have a specific value.
 *
 * @author scorpio
 * @since 1.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(ConditionOnProperty.class)
public @interface ConditionalOnProperty {

  /**
   * The name of the properties to test
   *
   * @return
   */
  String name();

  /**
   * The expected value for the properties<br/>
   * value must equalsIgnoreCase property if not empty
   *
   * @return
   */
  String value() default "";


  /**
   * The expected wildcard for the properties<br/>
   * the wildcard must match property by wildcard if not empty
   *
   * @return
   */
  String wildcard() default "";


  /**
   * The expected regex for the properties<br/>
   * regex must match property by regex if not empty
   *
   * @return
   */
  String regex() default "";


  /**
   * Specify if property must be set. Defaults to
   * {@code true}.
   *
   * @return
   */
  boolean required() default true;


  /**
   * Specify if inverse the result of matching. Defaults to
   * {@code false}.
   *
   * @return if should match if the property is missing
   */
  boolean inverse() default false;
}
