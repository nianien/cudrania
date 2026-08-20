package com.nianien.cudrania.jdbc.table;

import com.nianien.cudrania.core.collection.map.CaseInsensitiveMap;
import com.nianien.cudrania.core.reflection.BeanProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.nianien.cudrania.core.exception.ExceptionChecker.throwIf;
import static com.nianien.cudrania.core.exception.ExceptionChecker.throwIfNull;
import static com.nianien.cudrania.core.reflection.Reflections.beanProperties;

/**
 * 接口{@link DataTable}的默认实现
 *
 * @param <T>
 */
class DataTableImpl<T> implements DataTable<T> {
    private static Logger logger = Logger.getLogger(DataTableImpl.class.getName());
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
    //大小写不敏感: 各数据库返回的列名大小写不一(如H2默认大写), 需与实体列名不区分大小写匹配
    private Map<String, FieldProperty> fieldProperties = new CaseInsensitiveMap<>();

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
        //属性类型: 优先取getter返回类型, 无getter时用字段类型(避免field-only属性NPE)
        Class<?> propType = property.getGetter() != null
                ? property.getGetter().getReturnType() : property.getField().getType();
        fieldProperties.put(columnName, new FieldProperty(column, propType, property.getGetter(), property.getSetter()));
        //@Id可能标注在getter或field上, 二者都可能为null, 需分别判空
        boolean isId = (property.getGetter() != null && property.getGetter().isAnnotationPresent(Id.class))
                || (property.getField() != null && property.getField().isAnnotationPresent(Id.class));
        if (isId) {
            this.keyFields.add(columnName);
        }
    }


}