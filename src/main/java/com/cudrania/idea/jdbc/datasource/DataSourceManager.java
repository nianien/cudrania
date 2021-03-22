package com.cudrania.idea.jdbc.datasource;

import java.util.Map;

import javax.sql.DataSource;

/**
 * 数据源管理类<br/>
 *
 * @author skyfalling
 */
public class DataSourceManager {

    /**
     * 默认数据源名称
     */
    protected DataSource defaultSource;
    /**
     * 数据源映射
     */
    protected Map<String, DataSource> sourceMapping;

    /**
     * @param builder
     */
    public DataSourceManager(DataSourceBuilder builder) {
        this(builder.build());
    }

    /**
     * @param dataSourceMap
     */
    public DataSourceManager(Map<String, DataSource> dataSourceMap) {
        this.sourceMapping = dataSourceMap;
        for (DataSource dataSource : dataSourceMap.values()) {
            this.defaultSource = dataSource;
            break;
        }
    }


    /**
     * 获取默认的数据源
     *
     * @return DataSource
     */
    public DataSource getDataSource() {
        return defaultSource;
    }


    /**
     * 根据名称获取数据源
     *
     * @param name
     * @return DataSource
     */
    public DataSource getDataSource(String name) {
        return sourceMapping.get(name);
    }

}
