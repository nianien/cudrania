package com.cudrania.core.json;

import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * 敏感字段校验类
 *
 * @author liyifei
 */
public class Sensitive {

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
     * 提供字段判断和字段转换
     *
     * @param validator
     * @param castAsString
     */
    public Sensitive(Predicate<String> validator, boolean castAsString, BiFunction<String, Object, String> converter) {
        this.validator = validator;
        this.castAsString = castAsString;
        this.converter = converter;
    }


    /**
     * 提供字段判断
     *
     * @param regex
     */
    public Sensitive(String regex) {
        this(s -> s.matches(regex), true, (k, v) -> "****");
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
