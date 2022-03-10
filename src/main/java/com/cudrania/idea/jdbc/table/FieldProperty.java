package com.cudrania.idea.jdbc.table;

import lombok.Getter;

import java.lang.reflect.Method;
import java.sql.SQLType;

import static com.cudrania.core.reflection.Reflections.invoke;

/**
 * 字段相关属性信息,包括名称,sqlType类型,getter和setter方法<br/>
 * 字段的getter和setter方法名必须保持一致,且getter方法的返回类型为setter的参数类型
 */

public class FieldProperty<T> implements Comparable<FieldProperty<T>> {
    @Getter
    private final String name;
    @Getter
    private final Class<T> type;
    private final Method getter;
    private final Method setter;
    @Getter
    private final SQLType sqlType;

    public FieldProperty(String name, Class<T> type, SQLType sqlType, Method getter, Method setter) {
        this.name = name;
        this.type = type;
        this.getter = getter;
        this.setter = setter;
        this.sqlType = sqlType;
    }

    public FieldProperty(Column column, Class<T> type, Method getter, Method setter) {
        this(column.value(), type, column.sqlType(), getter, setter);
    }

    public void setValue(Object obj, T value) {
        invoke(setter, obj, value);
    }

    public T getValue(Object obj) {
        return (T) invoke(getter, obj);
    }

    @Override
    public int compareTo(FieldProperty o) {
        return this.name.compareTo(o.name);
    }
}