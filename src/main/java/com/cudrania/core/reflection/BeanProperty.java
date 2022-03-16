package com.cudrania.core.reflection;

import com.cudrania.core.arrays.ArrayUtils;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
        Property property = property();
        this.alias = property != null ? property.value() : name;
        Ignore ignore = ignore();
        this.ignore = ignore != null ? ignore.value() : false;
    }

    private Property property() {

        return ofNullable(getter)
                .map(m -> m.getAnnotation(Property.class))
                .filter(p -> !p.value().isEmpty())
                .orElse(ofNullable(field)
                        .map(f -> f.getAnnotation(Property.class))
                        .filter(p -> !p.value().isEmpty())
                        .orElse(null));
    }

    private Ignore ignore() {
        return ofNullable(getter)
                .map(m -> m.getAnnotation(Ignore.class))
                .orElse(
                        ofNullable(field)
                                .map(f -> f.getAnnotation(Ignore.class)).orElse(null)
                );
    }


    public Object getValue(Object owner) {
        return ofNullable(getter)
                .map(m -> Reflections.invoke(m, owner))
                .orElse(
                        ofNullable(field)
                                .map(f -> Reflections.getFieldValue(f, owner)).orElse(null)
                );
    }


    public void setValue(Object owner, Object value) {
        ofNullable(setter)
                .ifPresentOrElse(m -> Reflections.invoke(m, owner, value),
                        () -> ofNullable(field)
                                .ifPresent(f -> Reflections.setFieldValue(f, owner, value))
                );
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
        return ArrayUtils.array(list, annotationClass);
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