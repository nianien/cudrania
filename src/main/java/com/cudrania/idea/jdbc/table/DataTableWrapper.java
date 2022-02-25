package com.cudrania.idea.jdbc.table;

import com.cudrania.idea.jdbc.sql.DataField;

import java.util.List;
import java.util.Set;

/**
 * DataTable对象的包装器
 *
 * @author skyfalling
 */
public class DataTableWrapper<T> implements DataTable<T> {

    private DataTable table;

    /**
     * 构造方法
     *
     * @param table
     */
    public DataTableWrapper(DataTable table) {
        this.table = table;
    }


    @Override
    public String getName() {
        return table.getName();
    }

    @Override
    public Class getType() {
        return table.getType();
    }

    @Override
    public Set<String> getFieldNames() {
        return table.getFieldNames();
    }

    @Override
    public List<DataField> getFields(Object entity) {
        return table.getFields(entity);
    }

    @Override
    public List<DataField> getFields(Object entity, String... fieldNames) {
        return table.getFields(entity, fieldNames);
    }

    @Override
    public DataField getField(Object entity, String fieldName) {
        return table.getField(entity, fieldName);
    }

    @Override
    public void setField(Object entity, String fieldName, Object value) {
        table.setField(entity, fieldName, value);
    }

    @Override
    public String getFieldName(String fieldName) {
        return table.getFieldName(fieldName);
    }

    @Override
    public boolean hasField(String fieldName) {
        return table.hasField(fieldName);
    }

    @Override
    public Class getFieldType(String fieldName) {
        return table.getFieldType(fieldName);
    }

    @Override
    public DataField idField(T entity) {
        return table.idField(entity);
    }
}
