package com.cudrania.spring.condition;


import com.sm.audit.commons.condition.ConditionalOnProperties.Logic;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

    if (metadata.isAnnotated(ConditionalOnProperty.class.getName())) {

      MultiValueMap<String, Object> allAnnotationAttributesMap = metadata.getAllAnnotationAttributes(
              ConditionalOnProperty.class.getName());
      List<AnnotationAttributes> annotationAttributesList = annotationAttributesFromMultiValueMap(
              allAnnotationAttributesMap);
      return matches(context, annotationAttributesList.toArray(new AnnotationAttributes[0]), Logic.AND);
    }
    return false;
  }


  protected boolean matches(ConditionContext context, AnnotationAttributes[] conditionalOnProperties, Logic logic) {
    if (conditionalOnProperties.length == 0) {
      return true;
    }
    boolean matched = logic == Logic.AND ? true : false;
    for (AnnotationAttributes conditionalOnProperty : conditionalOnProperties) {
      boolean matchOne = new PropertySpec(context.getEnvironment(), conditionalOnProperty).matches();
      if (logic == Logic.AND) {
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

  private List<AnnotationAttributes> annotationAttributesFromMultiValueMap(
          MultiValueMap<String, Object> multiValueMap) {
    List<Map<String, Object>> maps = new ArrayList<>();
    for (Entry<String, List<Object>> entry : multiValueMap.entrySet()) {
      for (int i = 0; i < entry.getValue().size(); i++) {
        Map<String, Object> map;
        if (i < maps.size()) {
          map = maps.get(i);
        } else {
          map = new HashMap<>();
          maps.add(map);
        }
        map.put(entry.getKey(), entry.getValue().get(i));
      }
    }
    List<AnnotationAttributes> annotationAttributes = new ArrayList<>(
            maps.size());
    for (Map<String, Object> map : maps) {
      annotationAttributes.add(AnnotationAttributes.fromMap(map));
    }
    return annotationAttributes;
  }


  private static class PropertySpec {
    String name;
    String value;
    String wildcard;
    String regex;
    boolean required;
    boolean inverse;
    String property;

    PropertySpec(PropertyResolver propertyResolver, AnnotationAttributes attributes) {
      name = propertyResolver.resolvePlaceholders((String) attributes.get("name"));
      property = propertyResolver.getProperty(name);
      value = propertyResolver.resolvePlaceholders((String) attributes.get("value"));
      wildcard = propertyResolver.resolvePlaceholders((String) attributes.get("wildcard"));
      regex = propertyResolver.resolvePlaceholders((String) attributes.get("regex"));
      required = (Boolean) attributes.get("required");
      inverse = (Boolean) attributes.get("inverse");
    }


    public boolean matches() {
      boolean matched = true;
      if (property == null) {
        matched = !required;
      } else {
        if (StringUtils.isNotEmpty(value)) {
          matched &= value.equalsIgnoreCase(property);
        }
        if (StringUtils.isNotEmpty(wildcard)) {
          matched &= Wildcard.match(property, wildcard);
        }
        if (StringUtils.isNotEmpty(regex)) {
          matched &= property.matches(regex);
        }
      }
      return inverse ? !matched : matched;
    }

  }
}
