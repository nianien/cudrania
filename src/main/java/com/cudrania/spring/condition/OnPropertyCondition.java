package com.cudrania.spring.condition;


import com.nianien.core.text.RegexUtils;
import com.nianien.core.util.StringUtils;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.PropertyResolver;

/**
 * {@link Conditional} that checks if the specified property have a specific value.
 *
 * @author scorpio
 * @version 1.0.0
 */
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class OnPropertyCondition extends OnBaseCondition<ConditionalOnProperty> implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotationAttributes attributes) {
        PropertySpec spec = new PropertySpec(context, attributes);
        return spec.matches();
    }


    static class PropertySpec {
        String value;
        String wildcard;
        String regex;
        boolean required;
        boolean invert;
        String property;

        PropertySpec(ConditionContext context, AnnotationAttributes attributes) {
            PropertyResolver resolver = context.getEnvironment();
            property = resolver.getProperty(resolver.resolvePlaceholders((String) attributes.get("name")));
            value = resolver.resolvePlaceholders((String) attributes.get("value"));
            wildcard = resolver.resolvePlaceholders((String) attributes.get("wildcard"));
            regex = resolver.resolvePlaceholders((String) attributes.get("regex"));
            required = (Boolean) attributes.get("required");
            invert = (Boolean) attributes.get("invert");
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
                    matched &= RegexUtils.matchWildcard(wildcard, property);
                }
                if (StringUtils.isNotEmpty(regex)) {
                    matched &= property.matches(regex);
                }
            }
            return invert ? !matched : matched;
        }

    }
}
