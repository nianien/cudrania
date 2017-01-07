package com.cudrania.spring.condition;


import com.cudrania.spring.condition.ConditionOnProperty.PropertySpec;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;

/**
 * {@link Conditional} that checks if the specified properties matches in logic combination.
 *
 * @author scorpio
 * @version 1.0.0
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class ConditionOnProperties extends LogicCondition<ConditionalOnProperties, ConditionContext, AnnotationAttributes> implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotationAttributes attributes) {
    AnnotationAttributes[] values = (AnnotationAttributes[]) attributes.get("value");
    Logic logic = (Logic) attributes.get("logic");
    return matches(context, values, logic);
  }


  @Override
  protected boolean matchOne(ConditionContext context, AnnotationAttributes attributes) {
    return new PropertySpec(context, attributes).matches();
  }
}