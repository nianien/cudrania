package com.cudrania.idea.jdbc.table;

import com.cudrania.core.annotation.Ignore;
import com.cudrania.core.log.LoggerFactory;
import com.cudrania.core.reflection.Reflections;
import com.cudrania.core.utils.StringUtils;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import static com.cudrania.core.exception.ExceptionChecker.throwIf;
import static com.cudrania.core.exception.ExceptionChecker.throwIfNull;
import static com.cudrania.core.reflection.Reflections.getMethod;
import static com.cudrania.core.reflection.Reflections.getters;
import static com.cudrania.core.utils.StringUtils.decapitalize;

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
        getters(entityClass, method -> {
            addFieldProperty(method);
            return false;
        });
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
     * @param getter
     */
    private void addFieldProperty(Method getter) {
        if (getter.isAnnotationPresent(Ignore.class)) {
            return;
        }
        String getterName = getter.getName();
        String propertyName = decapitalize(getterName.substring(getterName.startsWith("is") ? 2 : 3));
        Field field = Reflections.getField(type, propertyName);
        if (field != null && field.isAnnotationPresent(Ignore.class)) {
            return;
        }
        Column column = TableHelper.getColumnName(getter, field);
        String columnName = column.value();
        throwIf(fieldProperties.containsKey(columnName), "duplicate field declared in table[" + type + "]: " + columnName);
        String setterName = "set" + StringUtils.capitalize(propertyName);
        //获取getter对应的setter方法
        Method setter = getMethod(type, setterName, getter.getReturnType());
        fieldProperties.put(columnName, new FieldProperty(column, getter.getReturnType(), getter, setter));
        if (getter.isAnnotationPresent(Id.class) || field.isAnnotationPresent(Id.class)) {
            this.keyFields.add(columnName);
        }
    }


}