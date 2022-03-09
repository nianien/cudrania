package com.cudrania.idea.jdbc.table;

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
    String[] getFieldNames();

    /**
     * 获取主键字段
     *
     * @return
     */
    String[] getKeys();

    /**
     * 查找匹配指定字段,如果不存在,返回null
     *
     * @param fieldName
     * @return
     */
    FieldProperty getField(String fieldName);


}
