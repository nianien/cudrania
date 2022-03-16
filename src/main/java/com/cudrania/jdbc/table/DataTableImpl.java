package com.cudrania.jdbc.table;

import com.cudrania.core.log.LoggerFactory;
import com.cudrania.core.reflection.BeanProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import static com.cudrania.core.exception.ExceptionChecker.throwIf;
import static com.cudrania.core.exception.ExceptionChecker.throwIfNull;
import static com.cudrania.core.reflection.Reflections.beanProperties;

/**
 * 接口{@link DataTable}的默认实现
 *
 * @param <T>
 */
class DataTableImpl<T> implements DataTable<T> {
    private static Logger logger = LoggerFactory.getLogger(DataTableImpl.class);
    /**
     * 表名
     */
    @Getter
    private String name;
    /**
     * 实体类型
     */
    @Getter
    private Class<T> type;
    /**
     * 字段属性列表
     */
    private Map<String, FieldProperty> fieldProperties = new TreeMap<>();

    /**
     * 主键字段
     */
    private List<String> keyFields = new ArrayList<>();


    /**
     * 构造方法
     *
     * @param entityClass
     */
    public DataTableImpl(Class<T> entityClass) {
        this.type = entityClass;
        this.name = TableHelper.getTableName(entityClass);
        beanProperties(entityClass).forEach(this::addFieldProperty);
        if (keyFields.isEmpty() && getField("id") != null) {
            keyFields.add(fieldProperty("id").getName());
        }
        if (keyFields.isEmpty()) {
            logger.warning("no id field defined in table[" + type + "]");
        }
    }


    @Override
    public String[] getFieldNames() {
        return fieldProperties.keySet().toArray(new String[0]);
    }

    @Override
    public String[] getKeys() {
        return keyFields.toArray(new String[0]);
    }

    @Override
    public FieldProperty getField(String fieldName) {
        return fieldProperties.get(fieldName);
    }

    /**
     * 获取字段属性
     *
     * @param fieldName
     * @return
     */
    private FieldProperty fieldProperty(String fieldName) {
        FieldProperty fieldProperty = fieldProperties.get(fieldName);
        throwIfNull(fieldProperty, new NoSuchFieldException("no such field declared in table[" + type + "]:" + fieldName));
        return fieldProperty;
    }

    /**
     * 添加字段属性方法
     *
     * @param property
     */
    private void addFieldProperty(BeanProperty property) {
        if (property.isIgnore()) {
            return;
        }
        Column column = TableHelper.getColumnName(property);
        String columnName = column.value();
        throwIf(fieldProperties.containsKey(columnName), "duplicate field declared in table[" + type + "]: " + columnName);
        fieldProperties.put(columnName, new FieldProperty(column, property.getGetter().getReturnType(), property.getGetter(), property.getSetter()));
        if (property.getGetter().isAnnotationPresent(Id.class) || property.getField().isAnnotationPresent(Id.class)) {
            this.keyFields.add(columnName);
        }
    }


}