package com.cudrania.core.json.serializer;

/**
 * JSON字段序列化脱敏(掩码)接口定义.
 * <p>
 * 注意: 这是<b>字段脱敏/掩码</b>处理, 并非密码学意义上的加密.
 *
 * @author liyifei
 */
public interface SerMasker<T> {

    /**
     * 判定字段是否需要脱敏处理
     *
     * @param name 字段名
     * @return
     */
    boolean shouldMask(String name);

    /**
     * 对字段内容进行脱敏处理
     *
     * @param name  字段名
     * @param value 字段值
     * @return
     */
    Object mask(String name, T value);


}
