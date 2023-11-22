package com.cudrania.core.json.serializer;

import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * 基于正则式进行加密字段判断和加密处理
 *
 * @author liyifei
 */
public class RegexSerEncryptor implements SerEncryptor<Object> {

    /**
     * 敏感字段判断
     */
    private Predicate<String> validator;

    /**
     * 字段转换函数
     */
    private BiFunction<String, Object, String> converter;

    /**
     * 是否进行类型转换
     */
    private boolean castAsString;


    /**
     * 加密字段判断和字段值转换
     *
     * @param validator
     * @param converter
     * @param castAsString
     */
    public RegexSerEncryptor(Predicate<String> validator, BiFunction<String, Object, String> converter, boolean castAsString) {
        this.validator = validator;
        this.castAsString = castAsString;
        this.converter = converter;
    }


    /**
     * 提供字段判断
     *
     * @param regex
     */
    public RegexSerEncryptor(String regex) {
        this(s -> s.matches(regex), (k, v) -> "****", true);
    }


    /**
     * 检查字段是否敏感字段
     *
     * @param name
     * @param value
     * @return
     */
    public boolean shouldEncrypt(String name, Object value) {
        return validator.test(name) && (castAsString || value instanceof String);
    }

    /**
     * 处理敏感字段
     *
     * @param name
     * @param value
     * @return
     */
    public String encrypt(String name, Object value) {
        return converter.apply(name, value);
    }
}
