package com.cudrania.core.json;

import com.cudrania.core.date.DateFormatter;
import com.cudrania.core.exception.ExceptionChecker;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JSON与Java对象相互转换的工具类
 *
 * @author skyfalling
 */
public class JsonParser {

    /**
     * 多态接口, 继承该接口的类,可以在序列化时加入类信息,使得反序列化时自动转换成对应类型
     */
    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY)
    interface Polymorphous {
    }

    /**
     * 内置的ObjectMapper实例
     */
    private ObjectMapper objectMapper;


    /**
     * 构造函数,使用默认的ObjectMapper实例, 拥有以下特性:<br>
     * 1) 允许字段名不使用引号<br>
     * 2) 允许字段名和字符串使用单引号<br>
     * 3) 允许数字含有前导符0<br>
     * 4) 允许有不存在的属性<br>
     * 5) 支持以下日期格式:<br>
     * "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "MM-dd","HH:mm:ss", "HH:mm"<p>
     */
    public JsonParser() {
        objectMapper = JsonMapper.builder()
                //不序列化null值
                .serializationInclusion(Include.NON_NULL)
                // 允许数字含有前导0
                .enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS).build()
                // 允许字段名不用引号
                .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                // 允许使用单引号
                .configure(Feature.ALLOW_SINGLE_QUOTES, true)
                .configure(Feature.STRICT_DUPLICATE_DETECTION, true)
                // 允许未知的属性
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                //支持字段序列化
                .setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        this.setDatePatterns(new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "MM-dd HH:mm"});
    }

    /**
     * 构造函数, 指定ObjectMapper对象
     *
     * @param objectMapper
     */
    public JsonParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 获取当前ObjectMapper对象
     *
     * @return
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * 设置识别的日期格式集合
     *
     * @param datePatterns
     * @return
     */
    public JsonParser setDatePatterns(final String[] datePatterns) {
        objectMapper.setDateFormat(new SimpleDateFormat(datePatterns[0]) {
            @Override
            public Date parse(String source) {
                try {
                    return DateFormatter.parseDate(source, datePatterns);
                } catch (Exception e) {
                    throw new IllegalArgumentException("date [" + source + "] should comply with one the formats:" + Arrays.toString(datePatterns), e);
                }
            }
        });
        return this;
    }

    /**
     * json转T对象
     * <pre>
     *     String json="{\"key\":[1,2,3]}";
     *     TypeReference ref = new TypeReference&lt;Map&lt;String,String[]&gt;&gt;() {};
     *     Map&lt;String,String[]&gt; map=toBean(json,ref)
     * </pre>
     *
     * @param json
     * @param typeReference
     * @param <T>
     * @return
     */
    public <T> T toBean(String json, TypeReference<T> typeReference) {
        return readValue(json, objectMapper.getTypeFactory().constructType(typeReference));
    }

    /**
     * json转Object对象, 根据json字符串的结构自动调整为对应的数据类型, 具体对应关系如下：<br>
     * 1)字符串-&gt;String类型<br>
     * 2)整数-&gt;int类型<br>
     * 3)长整数-&gt;long类型<br>
     * 4)实数-&gt;double类型 <br>
     * 5)键值对-&gt;(LinkedHash)Map类型<br>
     * 6)数组-&gt;(Array)List类型<br>
     *
     * @param json
     * @return
     */
    public Object toObject(String json) {
        return toBean(json, Object.class);
    }

    /**
     * json转T对象
     *
     * @param <T>
     * @param json
     * @param beanType
     * @return
     */
    public <T> T toBean(String json, Class<T> beanType) {
        try {
            return readValue(json, objectMapper.getTypeFactory().constructType(beanType));
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * json转T对象数组
     *
     * @param json
     * @param elementType
     * @return
     */
    public <T> T[] toArray(String json, Class<T> elementType) {
        try {
            return readValue(json,
                    objectMapper.getTypeFactory()
                            .constructArrayType(elementType));
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }

    }

    /**
     * json转List&lt;T&gt;对象
     *
     * @param <T>
     * @param json
     * @param elementType
     * @return
     */
    public <T> List<T> toList(String json, Class<T> elementType) {
        try {
            return readValue(json,
                    objectMapper.getTypeFactory()
                            .constructCollectionType(ArrayList.class, elementType));
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * json转Map&lt;K,V&gt;对象
     *
     * @param json
     * @param keyType
     * @param valueType
     * @return
     */
    public <K, V> Map<K, V> toMap(String json, Class<K> keyType, Class<V> valueType) {
        try {
            return readValue(json,
                    objectMapper.getTypeFactory()
                            .constructMapType(LinkedHashMap.class, keyType, valueType));
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 对象类型转JSON字符串
     *
     * @param obj
     * @return
     */
    @SneakyThrows
    public String toJson(Object obj) {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * 反序列化
     *
     * @param json
     * @param valueType
     * @param <T>
     * @return
     */
    @SneakyThrows
    private <T> T readValue(String json, JavaType valueType) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return objectMapper.readValue(json, valueType);
    }


}
