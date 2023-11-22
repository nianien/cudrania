package com.cudrania.core.json.serializer;

/**
 * 序列化加密接口定义
 * Created on 2022/2/18
 *
 * @author liyifei
 */
public interface SerEncryptor<T> {

    /**
     * 判定是否需要加密
     *
     * @param name  字段名
     * @param value 字段值
     * @return
     */
    boolean shouldEncrypt(String name, T value);

    /**
     * 对字段进行加密处理
     *
     * @param name  字段名
     * @param value 字段值
     * @return
     */
    Object encrypt(String name, T value);


}
