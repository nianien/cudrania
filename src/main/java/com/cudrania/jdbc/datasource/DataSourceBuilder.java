package com.cudrania.jdbc.datasource;


import com.cudrania.core.reflection.Reflections;
import com.cudrania.core.utils.StringUtils;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * 数据源构建对象, 自适应多种数据源,spring配置如下:
 * <pre>
 *     spring.datasource.config:
 *       properties:
 *         default:
 *           driver-class-name: com.mysql.jdbc.Driver
 *           type: com.zaxxer.hikari.HikariDataSource
 *           minimum-idle: 5
 *           maximum-pool-size: 15
 *           auto-commit: true
 *           idle-timeout: 30000
 *           pool-name: HikariDataSource
 *           max-lifetime: 1800000
 *           connection-timeout: 30000
 *           connection-test-query: SELECT 1
 *           data-source-properties:
 *             useUnicode: true
 *             characterEncoding: UTF8
 *             zeroDateTimeBehavior: round
 *             autoReconnect: true
 *         audit:
 *           username: root
 *           password: root
 *           jdbc-url: jdbc:mysql://127.0.0.01:3306/audit?autoReconnect=true
 * </pre>
 *
 * @author scorpio
 * @version 1.0.0
 */
public class DataSourceBuilder {

    /**
     * 默认配置
     */
    private final static String DEFAULT_NAME = "default";
    /**
     * 数据源配置
     */
    @Setter
    private final Map<String, Map<String, Object>> properties = new LinkedHashMap<>();

    /**
     * 添加默认配置
     *
     * @param properties
     * @return
     */
    public DataSourceBuilder addProperties(Map<String, Object> properties) {
        return addProperties(DEFAULT_NAME, properties);
    }

    /**
     * 添加命名配置
     *
     * @param name
     * @param properties
     * @return
     */
    public DataSourceBuilder addProperties(String name, Map<String, Object> properties) {
        this.properties.computeIfAbsent(name, (key) -> new HashMap<>()).putAll(properties);
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
        Reflections.populate(dataSource, config);
        return dataSource;
    }

    /**
     * 创建默认数据源
     *
     * @return
     */
    public DataSource build() {
        return build(DEFAULT_NAME);
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
     * 创建全部数据源列表
     *
     * @return
     */
    public LinkedHashMap<String, DataSource> buildAll() {
        return build(n -> true);
    }

    /**
     * 合并配置项
     *
     * @param name
     * @return
     */
    private Map<String, Object> renderConfig(String name) {
        if (!properties.containsKey(name)) {
            throw new IllegalArgumentException("datasource not defined: " + name);
        }
        Map<String, Object> config = new HashMap<>(properties.getOrDefault(DEFAULT_NAME, Collections.EMPTY_MAP));
        config.putAll(properties.get(name));
        Set<String> keys = new HashSet<>(config.keySet());
        for (String origin : keys) {
            String hump = StringUtils.camelCase(origin);
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

}