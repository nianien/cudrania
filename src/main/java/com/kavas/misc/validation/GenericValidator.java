package com.kavas.misc.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 泛型校验类,只校验匹配类型数据<br/>
 *
 * @author skyfalling
 */
public abstract class GenericValidator<T> implements Validator {

    private Class supportType;


    public GenericValidator() {
        this.supportType = getGenericType();
    }

    @Override
    public final boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public final void validate(Object target, Errors errors) {
        if (supportType.isInstance(target)) {
            this.doValidate((T) target, errors);
        }
    }

    abstract public void doValidate(T target, Errors errors);

    /**
     * 获取泛型类型
     *
     * @return
     */
    protected Class getGenericType() {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            if (pType.getActualTypeArguments().length > 0 &&
                    pType.getActualTypeArguments()[0] instanceof Class) {
                return (Class) pType.getActualTypeArguments()[0];
            }
        }
        return Object.class;
    }

}
