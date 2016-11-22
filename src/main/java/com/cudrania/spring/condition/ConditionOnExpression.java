package com.cudrania.spring.condition;

import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

/**
 * A Condition that evaluates a SpEL expression.
 *
 * @author scorpio
 * @version 1.0.0
 * @see ConditionalOnExpression
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class ConditionOnExpression implements Condition {
  /**
   * Determine if the condition matches.
   *
   * @param context  the condition context
   * @param metadata metadata of the {@link AnnotationMetadata class}
   *                 or {@link MethodMetadata method} being checked.
   * @return {@code true} if the condition matches and the component can be registered
   * or {@code false} to veto registration.
   */
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    String expression = (String) metadata
            .getAnnotationAttributes(ConditionalOnExpression.class.getName())
            .get("value");
    expression = wrapIfNecessary(expression);
    expression = context.getEnvironment().resolvePlaceholders(expression);
    ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
    BeanExpressionResolver resolver = (beanFactory != null)
            ? beanFactory.getBeanExpressionResolver() : null;
    BeanExpressionContext expressionContext = (beanFactory != null)
            ? new BeanExpressionContext(beanFactory, null) : null;
    if (resolver == null) {
      resolver = new StandardBeanExpressionResolver();
    }
    return (Boolean) resolver.evaluate(expression, expressionContext);
  }

  /**
   * Allow user to provide bare expression with no '#{}' wrapper.
   *
   * @param expression source expression
   * @return wrapped expression
   */
  private String wrapIfNecessary(String expression) {
    if (!expression.startsWith("#{")) {
      return "#{" + expression + "}";
    }
    return expression;
  }
}
