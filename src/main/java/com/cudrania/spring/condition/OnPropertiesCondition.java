package com.cudrania.spring.condition;


import com.cudrania.spring.condition.OnPropertyCondition.PropertySpec;

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
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class OnPropertiesCondition extends OnBaseCondition<ConditionalOnProperties>
        implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotationAttributes attributes) {
        return ((Operator) attributes.get("operator")).matches((AnnotationAttributes[]) attributes.get("value"), context, (v, c) -> new PropertySpec(c, v).matches());
    }


}