package com.cudrania.validation;

import org.hibernate.validator.internal.engine.ConstraintValidatorContextImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * {@link ConstraintValidator}接口的增强实现,针对基于{@link ConstraintValidatorContextImpl}的实现,支持动态修改注解的属性值
 *
 * @author skyfalling
 */
public abstract class EnhancedValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

    private Map<String, Object> attributesMap;

    public abstract void initialize(A constraintAnnotation);

    public abstract boolean isValid(T value, ConstraintValidatorContext context);

    /**
     * 添加属性
     *
     * @param context
     * @param name
     * @param value
     */
    public void addAttribute(ConstraintValidatorContext context, String name, Object value) {
        getAttributesMap(context).put(name, value);
    }

    /**
     * 删除属性
     *
     * @param context
     * @param name
     */
    public void removeAttribute(ConstraintValidatorContext context, String name) {
        getAttributesMap(context).remove(name);
    }


    /**
     * 添加属性
     *
     * @param context
     * @param attributesMap
     */
    public void addAttributes(ConstraintValidatorContext context, Map<String, Object> attributesMap) {
        getAttributesMap(context).putAll(attributesMap);
    }

    /**
     * 包装注解,以提供友好的消息提示
     *
     * @param annotations
     * @param type
     * @return
     */
    public AnnotationWrapper[] wrapper(Object[] annotations, Class type) {
        AnnotationWrapper[] wrappers = new AnnotationWrapper[annotations.length];
        int i = 0;
        for (Object annotation : annotations) {
            wrappers[i++] = new AnnotationWrapper(annotation, type);
        }
        return wrappers;
    }

    /**
     * 获取属性映射表
     *
     * @param context
     * @return
     */
    private Map<String, Object> getAttributesMap(ConstraintValidatorContext context) {
        if (this.attributesMap == null) {
            if (context instanceof ConstraintValidatorContextImpl) {
                try {
                    ConstraintValidatorContextImpl impl = (ConstraintValidatorContextImpl) context;
                    Map attributes = impl.getConstraintDescriptor().getAttributes();
                    Field field = attributes.getClass().getDeclaredField("m");
                    field.setAccessible(true);
                    this.attributesMap = (Map) field.get(attributes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (this.attributesMap == null)
            throw new UnsupportedOperationException("UnSupported Operation");
        return this.attributesMap;
    }

    private static class AnnotationWrapper {
        Object object;
        Class type;

        AnnotationWrapper(Object object, Class type) {
            this.object = object;
            this.type = type;
        }

        public String toString() {
            return object.toString().substring(type.getName().length() + 1);
        }
    }

}
