package com.cudrania.spring.condition;


import com.sm.audit.commons.condition.ConditionalOnProperties.Logic;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * {@link Conditional} that checks if the specified properties matches in logic combination.
 *
 * @author scorpio
 * @version 1.0.0
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class ConditionOnProperties extends ConditionOnProperty implements Condition {
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

    if (metadata.isAnnotated(ConditionalOnProperties.class.getName())) {
      Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnProperties.class.getName());
      AnnotationAttributes[] conditionalOnProperties = (AnnotationAttributes[]) attributes.get("value");
      Logic logic = (Logic) attributes.get("logic");
      return matches(context, conditionalOnProperties, logic);
    }
    return false;
  }


}