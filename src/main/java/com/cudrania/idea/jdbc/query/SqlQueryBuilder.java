package com.cudrania.idea.jdbc.query;

import com.cudrania.idea.jdbc.sql.SqlStatement;

import javax.sql.DataSource;

/**
 * 数据库访问接口实现,该类是线程安全的
 *
 * @author skyfalling
 */
public class SqlQueryBuilder {
    /**
     * 数据源
     */
    protected DataSource dataSource;

    /**
     * 指定数据源和SQL语句
     *
     * @param dataSource
     */
    public SqlQueryBuilder(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * 指定数据源和SQL语句
     */
    public SqlQuery build(SqlStatement sqlStatement) {
        return new SqlQueryImpl(dataSource, sqlStatement);
    }


    /**
     * 指定数据源和SQL语句
     */
    public SqlQuery build() {
        return build(null);
    }
}
