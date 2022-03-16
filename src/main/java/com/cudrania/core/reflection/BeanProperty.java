package com.cudrania.core.reflection;

import com.cudrania.core.arrays.ArrayUtils;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Optional.ofNullable;

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
        this.alias = alias();
        this.ignore = ignore();
    }

    /**
     * 根据getter方法或者字段声明的{@link Property}注解获取别名
     *
     * @return
     */
    private String alias() {
        return Arrays.stream(getAnnotations(Property.class)).filter(p -> !p.value().isEmpty())
                .map(p -> p.value())
                .findFirst().orElse(name);
    }

    /**
     * 根据getter方法或者字段是否声明{@link Ignore}注解判断是否需要忽略该属性
     *
     * @return
     */
    private boolean ignore() {
        return Arrays.stream(getAnnotations(Ignore.class))
                .map(p -> p.value())
                .findFirst().orElse(false);
    }

    /**
     * 获取属性值
     *
     * @param owner
     * @return
     */
    public Object getValue(Object owner) {
        if (getter != null) {
            return Reflections.invoke(getter, owner);
        }
        if (field != null) {
            return Reflections.getFieldValue(field, owner);
        }
        return null;
    }

    /**
     * 设置属性值
     *
     * @param owner
     * @param value
     */
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
        return Arrays.asList(getter, field).stream().filter(Objects::nonNull)
                .map(e -> e.getAnnotation(annotationClass))
                .filter(Objects::nonNull)
                .toArray(n -> (T[]) Array.newInstance(annotationClass, n));
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