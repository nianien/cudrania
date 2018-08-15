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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Condition} that checks for the presence or absence of specific beans.
 *
 * @author scorpio
 */
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class OnSingleBeanCondition extends OnBeanCondition implements ConfigurationCondition {


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
        if (metadata.isAnnotated(ConditionalOnSingleBean.class.getName())) {
            List<String> matching = getMatchingBeans(context, metadata, ConditionalOnSingleBean.class);
            return !matching.isEmpty() && hasSingleAutowireCandidate(context.getBeanFactory(), matching);
        }
        return false;
    }


    private boolean hasSingleAutowireCandidate(
            ConfigurableListableBeanFactory beanFactory, List<String> beanNames) {
        return (beanNames.size() == 1
                || getPrimaryBeans(beanFactory, beanNames)
                .size() == 1);
    }

    private List<String> getPrimaryBeans(ConfigurableListableBeanFactory beanFactory,
                                         List<String> beanNames) {
        List<String> primaryBeans = new ArrayList<String>();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = findBeanDefinition(beanFactory, beanName);
            if (beanDefinition != null && beanDefinition.isPrimary()) {
                primaryBeans.add(beanName);
            }
        }
        return primaryBeans;
    }

    private BeanDefinition findBeanDefinition(ConfigurableListableBeanFactory beanFactory,
                                              String beanName) {
        if (beanFactory.containsBeanDefinition(beanName)) {
            return beanFactory.getBeanDefinition(beanName);
        }
        if (beanFactory
                .getParentBeanFactory() instanceof ConfigurableListableBeanFactory) {
            return findBeanDefinition(((ConfigurableListableBeanFactory) beanFactory
                    .getParentBeanFactory()), beanName);
        }
        return null;

    }
}