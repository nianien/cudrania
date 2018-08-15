package com.cudrania.spring.condition;

import com.nianien.core.reflect.Generics;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link Conditional} matches by {@link AnnotationAttributes} instead of {@link AnnotatedTypeMetadata}
 *
 * @author scorpio
 * @version 1.0.0
 */

@Order(Ordered.LOWEST_PRECEDENCE - 100)
public abstract class OnBaseCondition<Annotation extends java.lang.annotation.Annotation> implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Class<Annotation> type = Generics.find(this.getClass(), OnBaseCondition.class, 0);
        if (metadata.isAnnotated(type.getName())) {
            return matches(context, getAnnotationAttributes(metadata, type));
        }
        return false;
    }

    /**
     * called by {@link #matches(ConditionContext, AnnotatedTypeMetadata)}
     *
     * @param context
     * @param attributes
     * @return
     */
    public abstract boolean matches(ConditionContext context, AnnotationAttributes attributes);

    /**
     * retrieve attributes from annotation
     *
     * @param metadata
     * @param type
     * @return
     */
    public AnnotationAttributes getAnnotationAttributes(AnnotatedTypeMetadata metadata, Class<Annotation> type) {
        return AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(type.getName()));
    }

}
