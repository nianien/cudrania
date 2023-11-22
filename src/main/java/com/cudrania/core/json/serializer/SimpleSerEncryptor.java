package com.cudrania.core.json.serializer;

import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * 字段判断和加密处理的默认实现
 *
 * @author liyifei
 */
public class SimpleSerEncryptor implements SerEncryptor {

    /**
     * 敏感字段判断
     */
    private Predicate<String> validator;

    /**
     * 内容转换函数
     */
    private BiFunction<String, Object, Object> converter;

    /**
     * 是否支持字段强制转换成字符串
     */
    private boolean castAsString;


    /**
     * 支持自定义的加密字段判断和处理
     *
     * @param validator    判断字段是否需要加密
     * @param converter    字段内容转换函数
     * @param castAsString 是否支持字段强制转换成字符串
     */
    public SimpleSerEncryptor(Predicate<String> validator, BiFunction<String, Object, Object> converter, boolean castAsString) {
        this.validator = validator;
        this.castAsString = castAsString;
        this.converter = converter;
    }


    /**
     * 基于正则式实现的加密字段判断和处理
     *
     * @param regex
     */
    public SimpleSerEncryptor(String regex) {
        this(s -> s.matches(regex), (k, v) -> "****", true);
    }

    public boolean shouldEncrypt(String name, Object value) {
        return validator.test(name) && (castAsString || value instanceof String);
    }

    public Object encrypt(String name, Object value) {
        return converter.apply(name, value);
    }
}
