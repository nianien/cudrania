package com.cudrania.spring.condition;


import com.cudrania.spring.condition.ConditionalOnProperty.Matches;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

import jodd.util.Wildcard;

/**
 * 基于{@link ConditionalOnProperty}的条件判断
 *
 * @author scorpio
 * @version 1.0.0
 */
public class ConditionOnProperty implements Condition {
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

    Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnProperty.class.getName());
    if (attributes == null) {
      return false;
    }

    String name = (String) attributes.get("name");
    String value = (String) attributes.get("value");
    Matches match = (Matches) attributes.get("match");
    boolean inverse = (Boolean) attributes.get("inverse");
    boolean caseIgnore = (Boolean) attributes.get("caseIgnore");

    String property = System.getProperty(name, "");
    if (caseIgnore) {
      value = value.toLowerCase();
      property = property.toLowerCase();
    }
    boolean matched = false;
    if (StringUtils.isNotEmpty(property)) {
      switch (match) {
        case EQUALS:
          matched = property.equals(value);
          break;
        case CONTAINS:
          matched = property.contains(value);
          break;
        case REGEX:
          matched = property.matches(value);
          break;
        case WILDCARD:
          matched = Wildcard.match(property, value);
          break;
      }
    }
    return inverse ? !matched : matched;
  }

}
