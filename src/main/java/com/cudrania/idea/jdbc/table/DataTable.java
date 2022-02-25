package com.cudrania.idea.jdbc.table;

import com.cudrania.idea.jdbc.sql.DataField;

import java.util.List;
import java.util.Set;

/**
 * 基于类定义的表结构信息<br/>
 * 表名称按优先级依次取 指定名称>@Table>类名称<br/>
 * 字段名按优先级依次取 @Column>@Property>getter方法<br/>
 * 另外,字段的getter和setter方法名必须保持一致,且getter方法的返回类型为setter的参数类型<br/>
 *
 * @author skyfalling
 */
public interface DataTable<T> {
    /**
     * 获取表名称
     *
     * @return
     */
    String getName();

    /**
     * 获取实体类型
     *
     * @return
     */
    Class<T> getType();

    /**
     * 获取字段名称列表
     *
     * @return
     */
    Set<String> getFieldNames();

    /**
     * 获取全部字段值
     *
     * @param entity
     * @return
     */
    List<DataField> getFields(T entity);

    /**
     * 获取指定字段值
     *
     * @param entity
     * @param fieldNames
     * @return
     */
    List<DataField> getFields(T entity, String... fieldNames);

    /**
     * 获取指定字段值
     *
     * @param entity
     * @param fieldName
     * @return
     */
    DataField getField(T entity, String fieldName);

    /**
     * 设定指定字段值
     *
     * @param entity
     * @param fieldName
     * @return
     */
    void setField(T entity, String fieldName, Object value);

    /**
     * 检查是否包含指定字段,如果包含,则返回字段初始名称,否则抛出异常信息
     *
     * @param fieldName
     * @return
     */
    String getFieldName(String fieldName);

    /**
     * 检查是否包含指定字段,如果包含,则返回true,否则返回false
     *
     * @param fieldName
     * @return
     */
    boolean hasField(String fieldName);

    /**
     * 获取指定字段类型
     *
     * @param fieldName
     * @return
     */
    Class getFieldType(String fieldName);


    /**
     * 获取键值字段
     *
     * @return
     */
    DataField idField(T entity);

}
