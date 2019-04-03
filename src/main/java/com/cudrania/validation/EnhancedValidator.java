package com.cudrania.validation;


import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * {@link ConstraintValidator}接口的增强实现,针对基于{@link ConstraintValidatorContextImpl}的实现,支持动态修改注解的属性值
 *
 * @author skyfalling
 */
public abstract class EnhancedValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

    /**
     * {@link ConstraintValidatorContext}对象内置的属性Map实例
     */
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
    protected void addAttribute(ConstraintValidatorContext context, String name, Object value) {
        //由于Hibernate Validation的bug,这里需要封装primitive类型数组
        getAttributesMap(context).put(name, wrapper(value));
    }

    /**
     * 添加属性集合
     *
     * @param context
     * @param attributesMap
     */
    protected void addAttributes(ConstraintValidatorContext context, Map<String, Object> attributesMap) {
        for (String name : attributesMap.keySet()) {
            addAttribute(context, name, attributesMap.get(name));
        }
    }


    /**
     * 删除属性
     *
     * @param context
     * @param name
     */
    protected void removeAttribute(ConstraintValidatorContext context, String name) {
        getAttributesMap(context).remove(name);
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

    /**
     * 包装primitive数组
     *
     * @param source
     * @return
     */
    private static Object wrapper(Object source) {
        if (source == null)
            return "";
        if (source instanceof Object[]) {
            return source;
        }
        if (source.getClass().isArray()) {
            int length = Array.getLength(source);
            if (length == 0) {
                return new Object[0];
            }
            Class<?> wrapperType = Array.get(source, 0).getClass();
            Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
            for (int i = 0; i < length; i++) {
                newArray[i] = Array.get(source, i);
            }
            return newArray;
        }
        return source;
    }


}
