package com.cudrania.core.json.serializer;

/**
 * 序列化加密接口定义
 * Created on 2022/2/18
 *
 * @author liyifei
 */
public interface SerEncryptor<T> {

    /**
     * 检查字段是否敏感字段
     *
     * @param name
     * @param value
     * @return
     */
    boolean shouldEncrypt(String name, Object value);

    /**
     * 对JSON字段进行加密
     *
     * @param value
     * @return
     */
    String encrypt(String key, T value);


}
