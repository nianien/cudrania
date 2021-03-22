package com.cudrania.idea.jdbc.query;

import com.cudrania.core.exception.ExceptionChecker;
import com.cudrania.core.io.Closer;
import com.cudrania.idea.jdbc.sql.SqlGenerator;
import com.cudrania.idea.jdbc.sql.SqlStatement;
import com.cudrania.idea.jdbc.table.DataField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * 数据库访问接口实现,该类是线程安全的
 *
 * @author skyfalling
 */
public class SqlQuery implements Query {
    /**
     * 数据源
     */
    protected DataSource dataSource;
    /**
     * SqlStatement对象
     */
    protected ThreadLocal<SqlStatement> sqlStatement = new ThreadLocal<SqlStatement>();

    /**
     * 构建方法,提供数据源
     *
     * @param dataSource
     */
    public SqlQuery(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * 获取当前SqlStatement对象
     *
     * @return
     */
    @Override
    public SqlStatement getSqlStatement() {
        ExceptionChecker.throwIfNull(sqlStatement.get(), new NullPointerException("SqlStatement required!"));
        return this.sqlStatement.get();
    }

    /**
     * 设置当前SqlStatement对象
     *
     * @param sqlStatement
     * @return
     */
    @Override
    public SqlQuery setSqlStatement(SqlStatement sqlStatement) {
        this.sqlStatement.set(sqlStatement);
        return this;
    }


    @Override
    public List<Object> getColumns(final int index) {
        return this.executeQuery(resultSet -> ResultSetAdapter.getColumns(resultSet, index));
    }

    @Override
    public List<Object> getColumns(final String name) {
        return executeQuery(resultSet -> ResultSetAdapter.getColumns(resultSet, name));
    }

    @Override
    public Map<Object, Object> getColumnsMap(final int keyIndex, final int valueIndex) {
        return executeQuery(resultSet -> ResultSetAdapter.getColumnsMap(resultSet, keyIndex, valueIndex));
    }

    @Override
    public Map<Object, Object> getColumnsMap(final String keyName, final String valueName) {
        return executeQuery(resultSet -> ResultSetAdapter.getColumnsMap(resultSet, keyName, valueName));
    }

    @Override
    public Map<String, Object> getFirstRow() {
        return executeQuery(resultSet -> ResultSetAdapter.getFirstRow(resultSet));
    }

    @Override
    public <T> T getFirstRow(final Class<T> clazz) {
        return executeQuery(resultSet -> ResultSetAdapter.getFirstRow(resultSet, clazz));
    }

    @Override
    public List<Map<String, Object>> getRows() {
        return executeQuery(resultSet -> ResultSetAdapter.getRows(resultSet));
    }

    @Override
    public <T> List<T> getRows(final Class<T> clazz) {
        return executeQuery(resultSet -> ResultSetAdapter.getRows(resultSet, clazz));
    }

    @Override
    public List<Map<String, Object>> getRows(final int start, final int size) {
        return executeQuery(resultSet -> ResultSetAdapter.getRows(resultSet, start, size));
    }

    @Override
    public <T> List<T> getRows(final Class<T> clazz, final int start, final int size) {
        return executeQuery(resultSet -> ResultSetAdapter.getRows(resultSet, start, size, clazz));
    }

    @Override
    public int getRowsCount() {
        return executeQuery(resultSet -> ResultSetAdapter.getRowsCount(resultSet));
    }

    @Override
    public <T> Map<Object, T> getRowsMap(final Class<T> clazz, final int index) {
        return executeQuery(resultSet -> ResultSetAdapter.getRowsMap(resultSet, index, clazz));
    }

    @Override
    public <T> Map<Object, T> getRowsMap(final Class<T> clazz, final String name) {
        return executeQuery(resultSet -> ResultSetAdapter.getRowsMap(resultSet, name, clazz));
    }

    @Override
    public <T> void insert(T bean) {
        this.setSqlStatement(SqlGenerator.insertSql(bean)).executeUpdate();
    }

    @Override
    public <T> void update(T bean, String... conditionFields) {
        this.setSqlStatement(SqlGenerator.updateSql(bean, conditionFields)).executeUpdate();
    }

    @Override
    public <T> void delete(T bean, String... conditionFields) {
        this.setSqlStatement(SqlGenerator.deleteSql(bean, conditionFields)).executeUpdate();
    }


    @Override
    public <T> T executeQuery(ResultSetHandler<T> rsh) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection connection = null;
        try {
            connection = connection();
            stmt = prepare(connection);
            return rsh.handle((rs = stmt.executeQuery()));
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        } finally {
            Closer.close(rs, stmt);
            releaseConnection(connection);
        }
    }

    @Override
    public int executeUpdate() {
        PreparedStatement stmt = null;
        Connection connection = null;
        try {
            connection = connection();
            stmt = prepare(connection);
            return stmt.executeUpdate();
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        } finally {
            Closer.close(stmt);
            releaseConnection(connection);
        }
    }


    @Override
    public int[] executeBatch(String... sqlList) {
        Connection connection = null;
        Statement stmt = null;
        try {
            connection = connection();
            stmt = connection.createStatement();
            for (String sql : sqlList) {
                stmt.addBatch(sql);
            }
            return stmt.executeBatch();
        } catch (SQLException e) {
            throw ExceptionChecker.throwException(e);
        } finally {
            Closer.close(stmt);
            releaseConnection(connection);
        }
    }


    @Override
    public int[] executeBatch(String sql, Object[][] parametersList) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = connection();
            stmt = connection.prepareStatement(sql);
            for (Object[] parameters : parametersList) {
                int i = 1;
                for (Object p : parameters) {
                    stmt.setObject(i++, p);
                }
                stmt.addBatch();
            }
            return stmt.executeBatch();
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        } finally {
            Closer.close(stmt);
            releaseConnection(connection);
        }

    }

    /**
     * 获取数据库连接,可以调用{@link #releaseConnection(java.sql.Connection)}方法释放连接
     *
     * @return
     */
    public Connection connection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 释放数据库连接
     *
     * @param connection
     */
    public void releaseConnection(Connection connection) {
        Closer.close(connection);
    }

    /**
     * 创建PreparedStatement对象
     *
     * @param connection
     * @return
     * @throws SQLException
     */
    protected PreparedStatement prepare(Connection connection) throws SQLException {
        SqlStatement sqlStatement = getSqlStatement();
        PreparedStatement stmt = connection.prepareStatement(sqlStatement.preparedSql());
        int i = 1;
        for (DataField field : sqlStatement.preparedParameters()) {
            if (field.type == null || field.type == DataField.GenericType) {
                stmt.setObject(i++, field.value);
            } else {
                stmt.setObject(i++, field.value, field.type.getVendorTypeNumber());
            }
        }
        return stmt;
    }

}
