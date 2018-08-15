package com.cudrania.spring.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A Condition that evaluates a SpEL expression.
 *
 * @author scorpio
 * @version 1.0.0
 * @see ConditionalOnExpression
 */
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class OnProfileCondition extends OnBaseCondition<ConditionalOnProfile> implements Condition {


    @Override
    public boolean matches(ConditionContext context, AnnotationAttributes attributes) {
        return ((Operator) attributes.get("operator"))
                .matches((String[]) attributes.get("value"),
                        new ProfilesSpec(context),
                        (v, c) -> c.matches(v));
    }


    static class ProfilesSpec {
        Set<String> activeProfiles;
        Set<String> defaultProfiles;

        ProfilesSpec(ConditionContext context) {
            Environment environment = context.getEnvironment();
            activeProfiles = new HashSet<>(Arrays.asList(environment.getActiveProfiles()));
            defaultProfiles = new HashSet<>(Arrays.asList(environment.getDefaultProfiles()));
        }

        boolean matches(String expression) {
            return ProfilesParser.parse(expression)
                    .matches(profile -> activeProfiles.contains(profile)
                            || activeProfiles.isEmpty() && defaultProfiles.contains(profile));
        }

    }
}
