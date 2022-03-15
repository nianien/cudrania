package com.cudrania.core.reflection;

import com.cudrania.core.collection.CollectionUtils;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BeanProperty {
    /**
     * 属性名
     */
    private String name;
    /**
     * 属性别称,主要是通过注解声明的属性名称
     */
    private String alias;
    private Method getter;
    private Method setter;
    /**
     * 对应的字段,字段名称需和name保持一致
     */
    private Field field;

    /**
     * 是否忽略
     */
    private boolean ignore;

    public BeanProperty(String name, Method getter, Method setter, Field field) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
        this.field = field;
        Property property = property();
        this.alias = property != null ? property.value() : name;
        Ignore ignore = ignore();
        this.ignore = ignore != null ? ignore.value() : false;
    }

    private Property property() {
        Property property = getter.getAnnotation(Property.class);
        if (property != null && !property.value().isEmpty()) {
            return property;
        }
        property = field.getAnnotation(Property.class);
        if (property != null && !property.value().isEmpty()) {
            return property;
        }
        return null;
    }

    private Ignore ignore() {
        Ignore ignore = getter.getAnnotation(Ignore.class);
        if (ignore != null) {
            return ignore;
        }
        return field.getAnnotation(Ignore.class);
    }


    public Object getValue(Object owner) {
        if (getter != null) {
            return Reflections.invoke(getter, owner);
        }
        if (field != null) {
            return Reflections.getFieldValue(field, owner);
        }
        return null;
    }


    public void setValue(Object owner, Object value) {
        if (setter != null) {
            Reflections.invoke(setter, owner, value);
        }
        if (field != null) {
            Reflections.setFieldValue(field, owner, value);
        }
    }


    /**
     * 获取指定类型的注解
     *
     * @param annotationClass
     * @param <T>
     * @return
     */
    public <T extends Annotation> T[] getAnnotations(Class<T> annotationClass) {
        List<T> list = new ArrayList<>();
        T t1 = getter.getAnnotation(annotationClass);
        if (t1 != null) {
            list.add(t1);
        }
        T t2 = field.getAnnotation(annotationClass);
        if (t2 != null) {
            list.add(t2);
        }
        return CollectionUtils.array(list, annotationClass);
    }

    @Override
    public String toString() {
        return "BeanProperty{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", getter=" + getter +
                ", setter=" + setter +
                ", field=" + field +
                '}';
    }
}