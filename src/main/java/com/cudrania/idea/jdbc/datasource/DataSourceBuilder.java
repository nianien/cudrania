package com.cudrania.idea.jdbc.datasource;


import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;

import javax.sql.DataSource;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 数据源构建对象, 自适应多种数据源
 *
 * @author scorpio
 * @version 1.0.0
 */
@Data
public class DataSourceBuilder {

    /**
     * 默认配置
     */
    private final static String DEFAULT_NAME = "default";
    /**
     * 默认数据源配置
     */
    private final Map<String, Object> defaultProperties = new LinkedHashMap<>();
    /**
     * 数据源配置
     */
    private final Map<String, Map<String, Object>> properties = new LinkedHashMap<>();


    /**
     * 添加命名配置
     *
     * @param name
     * @param properties
     * @return
     */
    public DataSourceBuilder addProperties(String name, Map<String, Object> properties) {
        if (DEFAULT_NAME.equals(name)) {
            defaultProperties.putAll(properties);
        } else {
            this.properties.computeIfAbsent(name, (key) -> new HashMap<>()).putAll(properties);
        }
        return this;
    }

    /**
     * 创建指定数据源
     *
     * @param name
     * @return
     */
    @SneakyThrows
    public DataSource build(String name) {
        Map<String, Object> config = renderConfig(name);
        Object dsType = config.get("type");
        Class<? extends DataSource> dsClass = null;
        if (dsType instanceof Class) {
            dsClass = (Class<? extends DataSource>) dsType;
        } else if (dsType instanceof String && !((String) dsType).isEmpty()) {
            dsClass = (Class<? extends DataSource>) Class.forName((String) dsType);
        }
        if (dsClass == null) {
            throw new IllegalArgumentException("datasource type is required!");
        }
        DataSource dataSource = dsClass.getDeclaredConstructor().newInstance();
        BeanUtils.populate(dataSource, config);
        return dataSource;
    }


    /**
     * 根据数据源名称选择创建数据源列表
     *
     * @param predicate 选择数据源名称
     * @return
     */
    public LinkedHashMap<String, DataSource> build(Predicate<String> predicate) {
        LinkedHashMap<String, DataSource> dataSourceMap = new LinkedHashMap<>();
        for (String name : properties.keySet()) {
            if (predicate.test(name)) {
                DataSource dataSource = build(name);
                dataSourceMap.put(name, dataSource);
            }
        }
        return dataSourceMap;
    }

    /**
     * 创建全部数据源列表,不包含默认数据源
     *
     * @return
     */
    public LinkedHashMap<String, DataSource> build() {
        return build((name) -> !DEFAULT_NAME.equalsIgnoreCase(name));
    }


    /**
     * 渲染配置项: 驼峰转换,Map-->Properties转换
     *
     * @param name
     * @return
     */
    private Map<String, Object> renderConfig(String name) {
        Map<String, Object> config = new HashMap<>(defaultProperties);
        if (!DEFAULT_NAME.equals(name) && properties.containsKey(name)) {
            config.putAll(properties.get(name));
        }
        Set<String> keys = new HashSet<>(config.keySet());
        for (String origin : keys) {
            String hump = hyphenToHump(origin);
            if (!config.containsKey(hump)) {
                config.put(hump, config.get(origin));
            }
        }
        for (Entry<String, Object> entry : config.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Properties properties = new Properties();
                properties.putAll((Map) entry.getValue());
                entry.setValue(properties);
            }
        }
        return config;
    }


    /**
     * 连字符转驼峰
     *
     * @param src
     * @return
     */
    private static String hyphenToHump(String src) {
        String dest = Arrays.stream(src.split("-"))
                .filter(e -> e.length() > 0)
                .map(e -> e.substring(0, 1).toUpperCase() + e.substring(1).toLowerCase())
                .collect(Collectors.joining());
        return dest.substring(0, 1).toLowerCase() + dest.substring(1);

    }

}