package com.cudrania.core.json.serializer;

/**
 * JSON字段序列化加密接口定义
 * Created on 2022/2/18
 *
 * @author liyifei
 */
public interface SerEncryptor<T> {

    /**
     * 判定是否需要处理
     *
     * @param name 字段名
     * @return
     */
    boolean shouldEncrypt(String name);

    /**
     * 对字段内容进行处理
     *
     * @param name  字段名
     * @param value 字段值
     * @return
     */
    Object encrypt(String name, T value);


}
