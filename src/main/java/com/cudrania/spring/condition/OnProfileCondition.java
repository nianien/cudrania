package com.cudrania.spring.condition;

import com.cudrania.spring.condition.ProfilesParser.Profiles;

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

        Environment environment = context.getEnvironment();
        Set<String> activeProfiles = new HashSet<>(Arrays.asList(environment.getActiveProfiles()));
        Set<String> defaultProfiles = new HashSet<>(Arrays.asList(environment.getDefaultProfiles()));
        String expression = (String) attributes.get("value");
        Profiles profiles = ProfilesParser.parse(expression);
        return profiles.matches(profile -> activeProfiles.contains(profile) || activeProfiles.isEmpty()
                && defaultProfiles.contains(profile));
    }


}
