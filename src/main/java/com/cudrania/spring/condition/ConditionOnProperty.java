package com.cudrania.spring.condition;


import com.cudrania.spring.condition.ConditionalOnProperty.MatchMode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

import jodd.util.Wildcard;

/**
 * 根据{@link ConditionalOnProperty}注解进行条件判断
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
    return matches(context, attributes);
  }


  /**
   * 判断是否匹配注解条件
   *
   * @param context
   * @param attributes
   * @return
   */
  public static boolean matches(ConditionContext context, Map<String, Object> attributes) {
    if (attributes == null) {
      return false;
    }
    String name = (String) attributes.get("name");
    String value = (String) attributes.get("value");
    MatchMode match = (MatchMode) attributes.get("match");
    boolean inverse = (Boolean) attributes.get("inverse");
    boolean caseIgnore = (Boolean) attributes.get("caseIgnore");
    boolean matched = false;

    String property = context.getEnvironment().getProperty(name, "");
    if (caseIgnore) {
      value = value.toLowerCase();
      property = property.toLowerCase();
    }
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
