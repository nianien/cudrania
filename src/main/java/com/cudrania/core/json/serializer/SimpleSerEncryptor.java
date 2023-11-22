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
     * 字段转换函数
     */
    private BiFunction<String, Object, String> converter;

    /**
     * 是否将加密字段强制转换成字符串
     */
    private boolean castAsString;


    /**
     * 支持自定义的加密字段判断和处理
     *
     * @param validator
     * @param converter
     * @param castAsString
     */
    public SimpleSerEncryptor(Predicate<String> validator, BiFunction<String, Object, String> converter, boolean castAsString) {
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

    public String encrypt(String name, Object value) {
        return converter.apply(name, value);
    }
}
