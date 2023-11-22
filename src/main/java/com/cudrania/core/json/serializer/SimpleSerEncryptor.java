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
     * 处理字段判断
     */
    private Predicate<String> validator;

    /**
     * 内容转换函数
     */
    private BiFunction<String, Object, Object> converter;


    /**
     * 自定义字段判断和处理
     *
     * @param validator 判断字段是否需要处理
     * @param converter BiFunction<字段名, 字段值, 转换后内容> 字段转换
     */
    public SimpleSerEncryptor(Predicate<String> validator, BiFunction<String, Object, Object> converter) {
        this.validator = validator;
        this.converter = converter;
    }


    /**
     * 基于正则式实现字段判断和处理
     *
     * @param regex     需要处理字段的正则式
     * @param converter BiFunction<字段名, 字段值, 转换后内容> 字段转换
     */
    public SimpleSerEncryptor(String regex, BiFunction<String, Object, Object> converter) {
        this(s -> s.matches(regex), converter);
    }


    /**
     * 基于正则式实现字段判断和处理
     *
     * @param regex       需要处理字段的正则式
     * @param replacement 字段内容替换
     */
    public SimpleSerEncryptor(String regex, String replacement) {
        this(regex, (k, v) -> replacement);
    }

    public boolean shouldEncrypt(String name) {
        return validator.test(name);
    }

    public Object encrypt(String name, Object value) {
        return converter.apply(name, value);
    }
}
