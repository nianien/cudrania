package com.cudrania.hibernate.sql;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 快速构建SQL的工具类<br/>如果构造方法{@link #SqlBuilder(String, boolean)}指定了缓存SQL参数的话,可以通过{@link #getParameters()}获取已缓存的SQL参数<br/>
 * 通常来讲,缓存SQL参数对于SQL语句使用JDBC占位符"?"时很有帮助<br/>
 *
 * @author skyfalling
 */
public class SqlBuilder {

    /**
     * SQL语句
     */
    private StringBuilder sqlBuffer = new StringBuilder();
    /**
     * 参数类表
     */
    private List<Object> parameters = new ArrayList<Object>();

    /**
     * 是否缓存SQL参数
     */
    private boolean cacheParameter;

    /**
     * 默认不缓存SQL参数
     */
    public SqlBuilder() {
    }

    /**
     * 构造方法,默认不缓存SQL参数
     *
     * @param sql 初始SQL
     */
    public SqlBuilder(String sql) {
        this(sql, false);
    }

    /**
     * 构造方法,指定是否缓存SQL参数
     *
     * @param sql            初始SQL
     * @param cacheParameter 指定是否缓存SQL参数
     */
    public SqlBuilder(String sql, boolean cacheParameter) {
        this.sqlBuffer.append(sql);
        this.cacheParameter = cacheParameter;
    }

    /**
     * 追加sql
     *
     * @param sql
     * @return
     */
    public SqlBuilder append(String sql) {
        sqlBuffer.append(sql);
        return this;
    }

    /**
     * 如果target对象不为null,则追加sql
     *
     * @param sql
     * @param target
     * @return
     */
    public SqlBuilder appendIfNotNull(String sql, Object target) {
        return appendIfTrue(sql, target != null, target);
    }

    /**
     * 如果target对象不为空,则追加sql
     *
     * @param sql
     * @param target
     * @return
     */
    public SqlBuilder appendIfNotEmpty(String sql, String target) {
        return appendIfTrue(sql, StringUtils.isNotEmpty(target), target);
    }

    /**
     * 如果target对象不为空,则追加sql
     *
     * @param sql
     * @param target
     * @param <T>
     * @return
     */
    public <T> SqlBuilder appendIfNotEmpty(String sql, Collection<T> target) {
        return appendIfTrue(sql, CollectionUtils.isNotEmpty(target), target);
    }

    /**
     * 若number非零,则追加SQL
     *
     * @param sql
     * @param number
     * @return
     */
    public SqlBuilder appendIfNotZero(String sql, Number number) {
        return appendIfTrue(sql, number != null && number.doubleValue() != 0, number);
    }

    /**
     * 若number正数,则追加SQL
     *
     * @param sql
     * @param number
     * @return
     */
    public SqlBuilder appendIfPositive(String sql, Number number) {
        return appendIfTrue(sql, number != null && number.doubleValue() > 0, number);
    }


    /**
     * 若number为负数,则追加SQL
     *
     * @param sql
     * @param number
     * @return
     */
    public SqlBuilder appendIfNegative(String sql, Number number) {
        return appendIfTrue(sql, number != null && number.doubleValue() < 0, number);
    }

    /**
     * 若number为零,则追加SQL
     *
     * @param sql
     * @param number
     * @return
     */
    public SqlBuilder appendIfZero(String sql, Number number) {
        return appendIfTrue(sql, number == null || number.doubleValue() == 0, number);
    }

    /**
     * 若number非正数,则追加SQL
     *
     * @param sql
     * @param number
     * @return
     */
    public SqlBuilder appendIfNotPositive(String sql, Number number) {
        return appendIfTrue(sql, number == null || number.doubleValue() <= 0, number);
    }

    /**
     * 若number非负数,则追加SQL
     *
     * @param sql
     * @param number
     * @return
     */
    public SqlBuilder appendIfNotNegative(String sql, Number number) {
        return appendIfTrue(sql, number == null || number.doubleValue() >= 0, number);
    }


    /**
     * 若expression为true,则追加SQL
     *
     * @param sql
     * @param expression
     * @param sqlParams
     * @return
     */
    public SqlBuilder appendIfTrue(String sql, boolean expression, Object... sqlParams) {
        if (expression) {
            sqlBuffer.append(sql);
            if (cacheParameter) {
                for (Object sqlParam : sqlParams) {
                    this.parameters.add(sqlParam);
                }
            }
        }
        return this;
    }

    /**
     * 获取SQL语句
     *
     * @return
     */
    public String toSql() {
        return sqlBuffer.toString();
    }

    /**
     * 获取SQL参数
     * 注:该方法在{@link #cacheParameter()}为true的时候有效
     *
     * @return
     * @see #cacheParameter()
     */
    public List<Object> getParameters() {
        return parameters;
    }

    /**
     * 是否缓存SQL参数
     *
     * @return
     */
    public boolean cacheParameter() {
        return cacheParameter;
    }

    @Override
    public String toString() {
        return "{sql=\"" +
                sqlBuffer + "\", parameters=" +
                parameters +
                "}";
    }

}
