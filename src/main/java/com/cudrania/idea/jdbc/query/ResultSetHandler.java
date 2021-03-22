package com.cudrania.idea.jdbc.query;


import java.sql.ResultSet;

/**
 * 声明处理数据库查询结果集的接口
 *
 * @param <T>
 * @author skyfalling
 */
public interface ResultSetHandler<T> {

    /**
     * 处理结果集
     *
     * @param resultSet
     * @return 返回处理结果
     * @throws UnsupportedOperationException
     */
    T handle(ResultSet resultSet) throws UnsupportedOperationException;

}
