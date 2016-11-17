package com.cudrania.spring.condition;


import com.sm.audit.commons.condition.ConditionalOnProperties.Logic;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * 根据{@link ConditionalOnProperty}注解进行条件判断
 *
 * @author scorpio
 * @version 1.0.0
 */
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