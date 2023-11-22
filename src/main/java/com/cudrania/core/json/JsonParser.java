package com.cudrania.core.json;

import com.cudrania.core.date.DateFormatter;
import com.cudrania.core.functions.Fn.Function;
import com.cudrania.core.json.serializer.SecurityPropertyFilter;
import com.cudrania.core.json.serializer.SerEncryptor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JSON与Java对象相互转换的工具类
 *
 * @author skyfalling
 */
public class JsonParser {

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
        this.withDatePatterns(new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "MM-dd HH:mm"});
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

    public JsonParser config(Function<ObjectMapper, ObjectMapper> config) {
        this.objectMapper = config.apply(this.objectMapper);
        return this;
    }

    /**
     * 注册模块
     *
     * @param module
     * @return
     */
    public JsonParser registerModule(Module module) {
        objectMapper.registerModule(module);
        return this;
    }

    /**
     * 设置字段可见性
     *
     * @param accessor
     * @param visibility
     * @return
     */
    public JsonParser setVisibility(PropertyAccessor accessor, Visibility visibility) {
        objectMapper.setVisibility(accessor, visibility);
        return this;
    }

    /**
     * 设置识别的日期格式集合
     *
     * @param datePatterns
     * @return
     */
    public JsonParser withDatePatterns(final String[] datePatterns) {
        objectMapper.setDateFormat(new SimpleDateFormat(datePatterns[0]) {
            @Override
            @SneakyThrows
            public Date parse(String source) {
                return DateFormatter.parseDate(source, datePatterns);
            }
        });
        return this;
    }

    /**
     * 设置字段序列化过滤器
     *
     * @param propertyFilter 字段过滤器, 可重写序列化内容
     * @return
     */
    public JsonParser withPropertyFilter(PropertyFilter propertyFilter) {
        String filterName = "filter-" + propertyFilter.getClass().getSimpleName();
        objectMapper.setFilterProvider(
                new SimpleFilterProvider().addFilter(filterName, propertyFilter)
        );
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public Object findFilterId(Annotated a) {
                return filterName;
            }
        });
        return this;
    }

    /**
     * 设置字段序列化改写器
     *
     * @param serModifier 序列化改写器, 可重写序列化内容
     * @return
     */
    public JsonParser withSerializerModifier(BeanSerializerModifier serModifier) {
        objectMapper.setSerializerFactory(
                objectMapper.getSerializerFactory().withSerializerModifier(serModifier)
        );
        return this;
    }

    /**
     * 修改字段序列化器
     *
     * @param rewriter 字段序列化器, 可重写序列化内容
     * @return
     */
    public JsonParser modifyPropertyWriter(Function<BeanPropertyWriter, BeanPropertyWriter> rewriter) {
        return withSerializerModifier(new BeanSerializerModifier() {
            @Override
            public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                             BeanDescription beanDesc,
                                                             List<BeanPropertyWriter> beanProperties) {
                //修改原有的BeanPropertyWriter列表
                return beanProperties.stream().map(rewriter)
                        .collect(Collectors.toList());
            }
        });
    }


    /**
     * 修改字段序列化器
     *
     * @param rewriter 字段序列化器, 可重写序列化内容
     * @return
     */
    public JsonParser modifySerializer(Function<JsonSerializer, JsonSerializer> rewriter) {
        return withSerializerModifier(new BeanSerializerModifier() {
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                return rewriter.apply(serializer);
            }
        });
    }


    /**
     * 设置字段序列化加密器
     *
     * @param encryptor 字段加密器
     * @return
     */
    public JsonParser withSerEncryptor(SerEncryptor encryptor) {
        return withPropertyFilter(new SecurityPropertyFilter(encryptor));
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
    @SneakyThrows
    public <T> T toBean(String json, Class<T> beanType) {
        return readValue(json, objectMapper.getTypeFactory().constructType(beanType));
    }

    /**
     * json转T对象数组
     *
     * @param json
     * @param elementType
     * @return
     */
    @SneakyThrows
    public <T> T[] toArray(String json, Class<T> elementType) {
        return readValue(json,
                objectMapper.getTypeFactory()
                        .constructArrayType(elementType));
    }

    /**
     * json转List&lt;T&gt;对象
     *
     * @param <T>
     * @param json
     * @param elementType
     * @return
     */
    @SneakyThrows
    public <T> List<T> toList(String json, Class<T> elementType) {
        return readValue(json,
                objectMapper.getTypeFactory()
                        .constructCollectionType(ArrayList.class, elementType));
    }

    /**
     * json转Map&lt;K,V&gt;对象
     *
     * @param json
     * @param keyType
     * @param valueType
     * @return
     */
    @SneakyThrows
    public <K, V> Map<K, V> toMap(String json, Class<K> keyType, Class<V> valueType) {
        return readValue(json,
                objectMapper.getTypeFactory()
                        .constructMapType(LinkedHashMap.class, keyType, valueType));
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
