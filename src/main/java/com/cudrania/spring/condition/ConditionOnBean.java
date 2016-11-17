/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cudrania.spring.condition;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link Condition} that checks for the presence or absence of specific beans.
 *
 * @author scorpio
 */
@Order(Ordered.LOWEST_PRECEDENCE)
class ConditionOnBean implements ConfigurationCondition {


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
    if (metadata.isAnnotated(ConditionalOnBean.class.getName())) {
      List<String> matching = getMatchingBeans(context, metadata, ConditionalOnBean.class);
      return !matching.isEmpty();
    }
    return false;
  }

  @Override
  public ConfigurationPhase getConfigurationPhase() {
    return ConfigurationPhase.REGISTER_BEAN;
  }


  protected List<String> getMatchingBeans(ConditionContext context, AnnotatedTypeMetadata metadata,
                                          Class<?> condition) {
    BeanSearchSpec beans = new BeanSearchSpec(context, metadata,
            condition);

    ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

    if (beanFactory == null) {
      return Collections.emptyList();
    }
    List<String> beanNames = new ArrayList<String>();
    for (String type : beans.getTypes()) {
      beanNames.addAll(getBeanNamesForType(beanFactory, type,
              context.getClassLoader()));
    }
    for (String ignoredType : beans.getIgnoredTypes()) {
      beanNames.removeAll(getBeanNamesForType(beanFactory, ignoredType,
              context.getClassLoader()));
    }
    for (String annotation : beans.getAnnotations()) {
      beanNames.addAll(Arrays.asList(getBeanNamesForAnnotation(beanFactory,
              annotation, context.getClassLoader())));
    }
    for (String beanName : beans.getNames()) {
      String resolvedName = context.getEnvironment().resolvePlaceholders(beanName);
      if (containsBean(beanFactory, resolvedName)) {
        beanNames.add(beanName);
      }
    }
    return beanNames;
  }

  private boolean containsBean(ConfigurableListableBeanFactory beanFactory,
                               String beanName) {
    return beanFactory.containsBean(beanName);
  }

  private Collection<String> getBeanNamesForType(ListableBeanFactory beanFactory,
                                                 String type, ClassLoader classLoader)
          throws LinkageError {
    try {
      Set<String> result = new LinkedHashSet<String>();
      collectBeanNamesForType(result, beanFactory,
              ClassUtils.forName(type, classLoader));
      return result;
    } catch (ClassNotFoundException ex) {
      return Collections.emptySet();
    }
  }

  private void collectBeanNamesForType(Set<String> result,
                                       ListableBeanFactory beanFactory, Class<?> type) {
    result.addAll(Arrays.asList(beanFactory.getBeanNamesForType(type)));
    if (beanFactory instanceof HierarchicalBeanFactory) {
      BeanFactory parent = ((HierarchicalBeanFactory) beanFactory)
              .getParentBeanFactory();
      if (parent instanceof ListableBeanFactory) {
        collectBeanNamesForType(result, (ListableBeanFactory) parent, type);
      }
    }
  }

  private String[] getBeanNamesForAnnotation(
          ConfigurableListableBeanFactory beanFactory, String type,
          ClassLoader classLoader) throws LinkageError {
    String[] result = {};
    try {
      @SuppressWarnings("unchecked")
      Class<? extends Annotation> typeClass = (Class<? extends Annotation>) ClassUtils
              .forName(type, classLoader);
      result = beanFactory.getBeanNamesForAnnotation(typeClass);
      if (beanFactory
              .getParentBeanFactory() instanceof ConfigurableListableBeanFactory) {
        String[] parentResult = getBeanNamesForAnnotation(
                (ConfigurableListableBeanFactory) beanFactory
                        .getParentBeanFactory(),
                type, classLoader);
        List<String> resultList = new ArrayList<String>();
        resultList.addAll(Arrays.asList(result));
        for (String beanName : parentResult) {
          if (!resultList.contains(beanName)
                  && !beanFactory.containsLocalBean(beanName)) {
            resultList.add(beanName);
          }
        }
        result = StringUtils.toStringArray(resultList);
      }
    } catch (ClassNotFoundException ex) {
    }
    return result;
  }


  protected static class BeanSearchSpec {

    private final Class<?> annotationType;

    private final List<String> names = new ArrayList<String>();

    private final List<String> types = new ArrayList<String>();

    private final List<String> annotations = new ArrayList<String>();

    private final List<String> ignoredTypes = new ArrayList<String>();


    BeanSearchSpec(ConditionContext context, AnnotatedTypeMetadata metadata,
                   Class<?> annotationType) {
      this.annotationType = annotationType;
      MultiValueMap<String, Object> attributes = metadata
              .getAllAnnotationAttributes(annotationType.getName(), true);
      collect(attributes, "name", this.names);
      collect(attributes, "value", this.types);
      collect(attributes, "type", this.types);
      collect(attributes, "annotation", this.annotations);
      collect(attributes, "ignored", this.ignoredTypes);
      collect(attributes, "ignoredType", this.ignoredTypes);
      BeanTypeDeductionException deductionException = null;
      try {
        if (this.types.isEmpty() && this.names.isEmpty()) {
          addDeducedBeanType(context, metadata, this.types);
        }
      } catch (BeanTypeDeductionException ex) {
        deductionException = ex;
      }
      validate(deductionException);
    }

    protected void validate(BeanTypeDeductionException ex) {
      if (!hasAtLeastOne(this.types, this.names, this.annotations)) {
        String message = annotationName()
                + " did not specify a bean using type, name or annotation";
        if (ex == null) {
          throw new IllegalStateException(message);
        }
        throw new IllegalStateException(message + " and the attempt to deduce"
                + " the bean's type failed", ex);
      }
    }

    private boolean hasAtLeastOne(List<?>... lists) {
      for (List<?> list : lists) {
        if (!list.isEmpty()) {
          return true;
        }
      }
      return false;
    }

    protected String annotationName() {
      return "@" + ClassUtils.getShortName(this.annotationType);
    }

    protected void collect(MultiValueMap<String, Object> attributes, String key,
                           List<String> destination) {
      List<?> values = attributes.get(key);
      if (values != null) {
        for (Object value : values) {
          if (value instanceof String[]) {
            Collections.addAll(destination, (String[]) value);
          } else {
            destination.add((String) value);
          }
        }
      }
    }

    private void addDeducedBeanType(ConditionContext context,
                                    AnnotatedTypeMetadata metadata, final List<String> beanTypes) {
      //继承类所在的注解
      if (metadata instanceof MethodMetadata
              && metadata.isAnnotated(Bean.class.getName())) {
        addDeducedBeanTypeForBeanMethod(context, beanTypes,
                (MethodMetadata) metadata);
      }
    }


    private void addDeducedBeanTypeForBeanMethod(ConditionContext context, final List<String> beanTypes,
                                                 final MethodMetadata methodMetadata) {
      try {
        // We should be safe to load at this point since we are in the
        // REGISTER_BEAN phase
        Class<?> configClass = ClassUtils.forName(
                methodMetadata.getDeclaringClassName(), context.getClassLoader());
        ReflectionUtils.doWithMethods(configClass, new MethodCallback() {
          @Override
          public void doWith(Method method)
                  throws IllegalArgumentException, IllegalAccessException {
            if (methodMetadata.getMethodName().equals(method.getName())) {
              beanTypes.add(method.getReturnType().getName());
            }
          }
        });
      } catch (Throwable ex) {
        throw new BeanTypeDeductionException(
                methodMetadata.getDeclaringClassName(),
                methodMetadata.getMethodName(), ex);
      }
    }

    public List<String> getNames() {
      return this.names;
    }

    public List<String> getTypes() {
      return this.types;
    }

    public List<String> getAnnotations() {
      return this.annotations;
    }

    public List<String> getIgnoredTypes() {
      return this.ignoredTypes;
    }

    @Override
    public String toString() {
      StringBuilder string = new StringBuilder();
      string.append("(");
      if (!this.names.isEmpty()) {
        string.append("names: ");
        string.append(StringUtils.collectionToCommaDelimitedString(this.names));
        if (!this.types.isEmpty()) {
          string.append("; ");
        }
      }
      if (!this.types.isEmpty()) {
        string.append("types: ");
        string.append(StringUtils.collectionToCommaDelimitedString(this.types));
      }
      string.append(")");
      return string.toString();
    }

  }

  private static class SingleCandidateBeanSearchSpec extends BeanSearchSpec {

    SingleCandidateBeanSearchSpec(ConditionContext context,
                                  AnnotatedTypeMetadata metadata, Class<?> annotationType) {
      super(context, metadata, annotationType);
    }

    @Override
    protected void collect(MultiValueMap<String, Object> attributes, String key,
                           List<String> destination) {
      super.collect(attributes, key, destination);
      destination.removeAll(Arrays.asList("", Object.class.getName()));
    }

    @Override
    protected void validate(BeanTypeDeductionException ex) {
      Assert.isTrue(getTypes().size() == 1, annotationName() + " annotations must "
              + "specify only one type (got " + getTypes() + ")");
    }
  }

  static final class BeanTypeDeductionException extends RuntimeException {

    private BeanTypeDeductionException(String className, String beanMethodName,
                                       Throwable cause) {
      super("Failed to deduce bean type for " + className + "." + beanMethodName,
              cause);
    }

  }

}