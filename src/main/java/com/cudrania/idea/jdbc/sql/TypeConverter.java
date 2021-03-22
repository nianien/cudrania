package com.cudrania.idea.jdbc.sql;

/**
 * 类型转换接口声明
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 */
public interface TypeConverter<S, T> {
    /**
     * 将指定对象转换为目标类型
     *
     * @param value 待转换对象
     * @return 转换后对象
     */
    T convert(S value);
}
