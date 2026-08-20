package com.nianien.cudrania.core.utils;

import com.nianien.cudrania.core.exception.ExceptionChecker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 枚举工具类
 *
 * @author skyfalling
 */
public class Enums {


    /**
     * 返回名称为name的枚举值<p>
     * 如果不存在则返回默认值defaultEnum
     *
     * @param enumClass  枚举类
     * @param name       枚举名称
     * @param defaultEnum  默认枚举类型
     * @param <T>        枚举类型
     * @return
     */
    public static <T extends Enum<T>> T of(Class<T> enumClass, String name, T defaultEnum) {
        T t = of(enumClass, name);
        return t != null ? t : defaultEnum;
    }

    /**
     * 返回名称为name的枚举值<p>
     * 如果不存在则返回null
     *
     * @param enumClass  枚举类
     * @param name       枚举名称
     * @param <T>        枚举类型
     * @return
     */
    public static <T extends Enum<T>> T of(Class<T> enumClass, String name) {
        T[] types = enumClass.getEnumConstants();
        for (T type : types) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }


    /**
     * 返回"value"字段值为value的枚举值<p>
     * 如果不存在则返回默认值defaultEnum
     *
     * @param enumClass  枚举类
     * @param value       枚举名称
     * @param defaultEnum  默认枚举类型
     * @param <T>        枚举类型
     * @return
     */
    public static <T extends Enum<T>> T with(Class<T> enumClass, Object value, T defaultEnum) {
        return with(enumClass, "value", value, defaultEnum);
    }

    /**
     * 返回"value"字段值为value的枚举值<p>
     * 如果不存在则返回null
     *
     * @param enumClass  枚举类
     * @param value       枚举名称
     * @param <T>        枚举类型
     * @return
     */
    public static <T extends Enum<T>> T with(Class<T> enumClass, Object value) {
        return with(enumClass, "value", value);
    }

    /**
     * 根据字段值返回相应的枚举对象<p>
     * 如果不存在则返回默认值defaultEnum
     *
     * @param enumClass  枚举类
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @param <T>        枚举类型
     * @return
     */
    public static <T extends Enum<T>> T with(Class<T> enumClass, String fieldName, Object fieldValue, T defaultEnum) {
        T t = with(enumClass, fieldName, fieldValue);
        return t != null ? t : defaultEnum;
    }

    /**
     * 根据字段值返回相应的枚举对象<p>
     * 如果没有对应的字段或字段值返回null
     *
     * @param enumClass  枚举类
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @param <T>        枚举类型
     * @return
     */
    public static <T extends Enum<T>> T with(Class<T> enumClass, String fieldName, Object fieldValue) {
        Field field;
        try {
            field = enumClass.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            //按契约: 无此字段返回null(不再打印噪音)
            return null;
        }
        for (T t : enumClass.getEnumConstants()) {
            try {
                //Objects.equals 兼容字段值为null的枚举常量, 避免NPE
                if (Objects.equals(field.get(t), fieldValue)) {
                    return t;
                }
            } catch (IllegalAccessException e) {
                //已setAccessible, 正常不会到这; 若真发生是反射错误, 抛出而非静默吞掉
                throw ExceptionChecker.throwException(e);
            }
        }
        //按契约: 无匹配返回null(不再打印噪音)
        return null;
    }


    /**
     * 取枚举对象除excludes之外的实例
     *
     * @param enumClass  枚举类
     * @param excludes   需忽略的枚举实例
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> List<T> except(Class<T> enumClass, T... excludes) {
        List<T> list = new ArrayList<>(Arrays.asList(enumClass.getEnumConstants()));
        for (T exclude : excludes) {
            list.remove(exclude);
        }
        return list;
    }

    /**
     * 取枚举对象除exclude和others之外的实例<p>
     * 该方法等价于
     * <pre>
     * <code>List list=Arrays.asList(others);
     * list.add(exclude)
     * EnumUtils.list(exclude.getDeclaringClass(),list.toArray())
     * </code>
     * </pre>
     *
     * @param exclude  需忽略的枚举实例
     * @param others   需忽略的其他枚举实例
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> List<T> except(T exclude, T... others) {
        List<T> list = new ArrayList<>(Arrays.asList(exclude.getDeclaringClass().getEnumConstants()));
        list.remove(exclude);
        for (T other : others) {
            list.remove(other);
        }
        return list;
    }
}
