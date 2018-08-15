package com.cudrania.spring.condition;

import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;

/**
 * A Condition that evaluates more than one SpEL expression.
 *
 * @author scorpio
 * @version 1.0.0
 * @see ConditionalOnExpression
 */
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class OnExpressionCondition extends OnBaseCondition<ConditionalOnExpression> implements Condition {


    @Override
    public boolean matches(ConditionContext context, AnnotationAttributes attributes) {
        return ((Operator) attributes.get("operator"))
                .matches((String[]) attributes.get("value"),
                        new ExpressionSpec(context),
                        (v, c) -> c.evaluate(v));
    }


    static class ExpressionSpec {
        ConditionContext context;
        BeanExpressionResolver resolver;
        BeanExpressionContext expressionContext;

        public ExpressionSpec(ConditionContext context) {
            this.context = context;
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            this.resolver = (beanFactory != null)
                    ? beanFactory.getBeanExpressionResolver() : null;
            this.expressionContext = (beanFactory != null)
                    ? new BeanExpressionContext(beanFactory, null) : null;
            if (resolver == null) {
                this.resolver = new StandardBeanExpressionResolver();
            }
        }

        public Boolean evaluate(String value) {
            if (!value.startsWith("#{")) {
                value = "#{" + value + "}";
            }
            value = context.getEnvironment().resolvePlaceholders(value);
            return (Boolean) resolver.evaluate(value, expressionContext);
        }

    }

}
