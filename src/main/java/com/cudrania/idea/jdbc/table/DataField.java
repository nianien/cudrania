package com.cudrania.idea.jdbc.table;

import java.sql.JDBCType;
import java.sql.SQLType;

/**
 * 字段类型
 *
 * @author skyfalling
 */
public class DataField {

    public static final SQLType GenericType = JDBCType.JAVA_OBJECT;
    /**
     * 字段名
     */
    public final String name;
    /**
     * 字段值
     */
    public final Object value;
    /**
     * 字段类型
     */
    public final SQLType type;

    /**
     * @param name
     * @param value
     * @param sqlType
     */
    public DataField(String name, Object value, SQLType sqlType) {
        this.name = name;
        this.value = value;
        this.type = sqlType;
    }

    public DataField(String name, Object value) {
        this(name, value, GenericType);
    }


    @Override
    public String toString() {
        return "DataField{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", type=" + type +
                '}';
    }
}
