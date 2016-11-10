package com.cudrania.spring.condition;


import com.cudrania.spring.condition.ConditionalOnProperties.Logical;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * 根据{@link ConditionalOnProperties}注解进行条件判断
 *
 * @author scorpio
 * @version 1.0.0
 */
public class ConditionOnProperties implements Condition {
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnProperties.class.getName());
    if (attributes == null) {
      return false;
    }
    AnnotationAttributes[] conditionalOnProperties = (AnnotationAttributes[]) attributes.get("value");
    Logical logic = (Logical) attributes.get("conjunction");
    boolean matched = logic == Logical.AND ? true : false;
    for (AnnotationAttributes conditionalOnProperty : conditionalOnProperties) {
      boolean matchOne = ConditionOnProperty.matches(context, conditionalOnProperty);
      if (logic == Logical.AND) {
        matched &= matchOne;
        if (!matched) {
          break;
        }
      } else {
        matched |= matchOne;
        if (matched) {
          break;
        }
      }
    }
    return matched;


  }


}
