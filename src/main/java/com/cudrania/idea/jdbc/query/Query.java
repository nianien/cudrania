package com.cudrania.idea.jdbc.query;

import com.cudrania.idea.jdbc.sql.SqlStatement;

import java.util.List;
import java.util.Map;


/**
 * 数据库访问接口定义
 *
 * @author skyfalling
 */
public interface Query {

    /**
     * 执行更新语句
     *
     * @return
     */
    int executeUpdate();

    /**
     * 执行查询,返回ResultSetHandler接口实例对象rsh对查询的处理结果
     *
     * @param rsh
     * @return
     */
    public <T> T executeQuery(ResultSetHandler<T> rsh);

    /**
     * 获取结果集中索引值为index的列
     *
     * @param index
     * @return
     */
    List<Object> getColumns(int index);

    /**
     * 获取结果集中名称为name的列
     *
     * @param name
     * @return
     */
    List<Object> getColumns(String name);

    /**
     * 将结果集中指定的两列映射成Map对象<br>
     * 其中索引值为keyIndex的列作为key,索引值为valueIndex的列作为value
     *
     * @param keyIndex
     * @param valueIndex
     * @return
     */
    Map<Object, Object> getColumnsMap(int keyIndex, int valueIndex);

    /**
     * 将结果集中指定的两列映射成Map对象<br>
     * 其中名称为keyName的列作为key,名称为valueName的列作为value
     *
     * @param keyName
     * @param valueName
     * @return
     */
    Map<Object, Object> getColumnsMap(String keyName, String valueName);

    /**
     * 获取结果集中的第一行数据<br>
     * 数据由Map&lt;String, Object>对象表示
     *
     * @return
     */
    Map<String, Object> getFirstRow();

    /**
     * 获取结果集中的第一行数据<br>
     * 数据由Class&lt;T>对象表示
     *
     * @param clazz
     * @return
     */
    <T> T getFirstRow(Class<T> clazz);

    /**
     * 获取由Map&lt;String, Object>对象表示的查询结果
     *
     * @return
     */
    List<Map<String, Object>> getRows();

    /**
     * 获取由Class&lt;T>对象表示的查询结果
     *
     * @param clazz
     * @return
     */
    <T> List<T> getRows(Class<T> clazz);

    /**
     * 获取查询结果中从第start条开始共size条的数据<br>
     * 其中start起始值为1
     *
     * @param start
     * @param size
     * @return
     */
    List<Map<String, Object>> getRows(int start, int size);

    /**
     * 获取查询结果中从第start条开始共size条的数据<br>
     * 其中start起始值为1
     *
     * @param clazz
     * @param start
     * @param size
     * @return
     */
    <T> List<T> getRows(Class<T> clazz, int start, int size);

    /**
     * 返回查询结果记录的数目<br>
     *
     * @return
     */
    int getRowsCount();

    /**
     * 以索引值为index的列为键值,将查询结果映射成Map&lt;Object,T>对象<br>
     *
     * @param clazz
     * @param index
     * @return Map&lt;String, T>
     */
    <T> Map<Object, T> getRowsMap(Class<T> clazz, int index);

    /**
     * 以名称为name的列为键值,将查询结果映射成Map&lt;Object,T>对象<br>
     *
     * @param name
     * @param clazz
     * @return Map&lt;Object, T>
     */
    <T> Map<Object, T> getRowsMap(Class<T> clazz, String name);

    /**
     * 根据实例对象插入记录,只写入非空字段<br>
     *
     * @param <T>
     * @param bean 实体对象
     */
    <T> void insert(T bean);

    /**
     * 根据实例对象的条件字段更新记录,属性值为空的字段不更新<br>
     *
     * @param <T>
     * @param bean            实体对象
     * @param conditionFields 条件字段,不分大小写
     */
    <T> void update(T bean, String... conditionFields);

    /**
     * 根据实例对象的条件字段删除记录,如未指定则使用全部字段<br>
     *
     * @param bean            实体对象
     * @param conditionFields 条件字段,不分大小写
     */
    <T> void delete(T bean, String... conditionFields);

    /**
     * 批量执行SQL语句
     *
     * @param sqlList
     * @return
     */
    int[] executeBatch(String... sqlList);

    /**
     * 批量执行SQL语句
     *
     * @param sql
     * @param parametersList 多组SQL参数
     * @return
     */
    int[] executeBatch(String sql, Object[][] parametersList);

    /**
     * 设置当前SqlStatement对象
     *
     * @param sqlStatement
     * @return
     */
    SqlQuery setSqlStatement(SqlStatement sqlStatement);

    /**
     * 获取当前SqlStatement对象
     *
     * @return
     */
    SqlStatement getSqlStatement();

}
