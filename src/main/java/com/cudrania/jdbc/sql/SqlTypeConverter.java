package com.cudrania.jdbc.sql;

import com.cudrania.core.arrays.ArrayUtils;
import com.cudrania.core.date.DateFormatter;
import com.cudrania.core.date.DatePattern;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 将SQL参数转换为字符串,用于拼装SQL语句<p>
 *
 * @author skyfalling
 */
public class SqlTypeConverter implements TypeConverter<Object, String> {

    /**
     * 已注册的转换器
     */
    private Map<Class, TypeConverter<?, String>> converters = new HashMap<>();
    private String datePattern = DatePattern.Default;
    private int scale;

    /**
     *
     */
    public SqlTypeConverter() {
        register(Date.class, value -> DateFormatter.format(value, datePattern));
        register(Number.class, number -> {
            if (scale > 0)
                if (number instanceof Float || number instanceof Double) {
                    return new BigDecimal(number.doubleValue()).setScale(scale, RoundingMode.HALF_UP).toString();
                }
            return number.toString();
        });
        register(Boolean.class, value -> value ? "1" : "0");
    }

    @Override
    public final String convert(Object value) {
        if (value == null)
            return "";
        if (value instanceof Collection) {
            return convert(((Collection) value).toArray(new Object[0]));
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            String[] array = new String[length];
            for (int i = 0; i < length; i++) {
                array[i] = this.convert(Array.get(value, i));
            }
            return ArrayUtils.toString(array, ",");
        }
        TypeConverter<Object, String> converter = find(value.getClass());
        if (converter != null)
            return converter.convert(value);
        return value.toString();
    }


    /**
     * 查找匹配的转换器
     *
     * @param type
     * @return
     */
    private TypeConverter find(Class type) {
        TypeConverter converter = converters.get(type);
        if (converter != null) {
            return converter;
        }
        for (Entry<Class, TypeConverter<?, String>> e : converters.entrySet()) {
            if (e.getKey().isAssignableFrom(type)) {
                return e.getValue();
            }
        }
        return null;
    }


    /**
     * 设置日期格式化
     *
     * @param datePattern
     */
    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    /**
     * 设置浮点数精度
     *
     * @param scale
     */
    public void setScale(int scale) {
        this.scale = scale;
    }


    /**
     * 注册指定类型的转换器
     *
     * @param type
     * @param converter
     * @param <T>
     */
    public <T> void register(Class<T> type, TypeConverter<T, String> converter) {
        if (converter != null) {
            this.converters.put(type, converter);
        }
    }
}
