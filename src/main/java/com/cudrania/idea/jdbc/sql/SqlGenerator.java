package com.cudrania.idea.jdbc.sql;

import com.cudrania.core.arrays.ArrayUtils;
import com.cudrania.idea.jdbc.table.DataTable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.cudrania.core.exception.ExceptionChecker.throwIf;
import static com.cudrania.idea.jdbc.table.DataTableFactory.get;

/**
 * SqlStatement对象的生成工具<br/>
 * 根据实体对象或者字段列表构建条件匹配的select,insert,update,delete等SQL语句<br/>
 * 注:这里条件字段只匹配以下运算符: "="(非空字段),"is null"(空值字段),"in"(数组或集合)<br/>
 * 该工具类根据实体类型声明的注解{@link com.cudrania.idea.jdbc.table.Table}和{@link com.cudrania.idea.jdbc.table.Column}获取{@link DataTable}对象,从而获取表名和字段名等信息
 *
 * @author skyfalling
 * @see DataTable
 * @see com.cudrania.idea.jdbc.table.Table
 * @see com.cudrania.idea.jdbc.table.Column
 */
public class SqlGenerator {


    /**
     * 构建select语句, 使用主键作为查询条件<br/>
     *
     * @param entity 实体对象
     * @param <T>
     * @return
     */
    public static <T> SqlStatement selectByKey(T entity) {
        DataTable<T> table = get(entity.getClass());
        return selectSql(entity, table.getKeys());
    }

    /**
     * 构建select语句<p/>
     * 如果条件字段为空, 则按非空属性查询
     *
     * @param entity          实体对象
     * @param conditionFields 条件字段
     * @param <T>
     * @return
     */
    public static <T> SqlStatement selectSql(T entity, String... conditionFields) {
        DataTable<T> table = get(entity.getClass());
        List<DataField> dataFields = getFields(entity, conditionFields.length > 0 ? conditionFields : table.getFieldNames(), conditionFields.length > 0);
        return selectSql(table.getName(), dataFields);
    }

    /**
     * 构建select语句<p/>
     * 查询条件conditions是一个{@link DataField}列表,其中{@link DataField#name}作为字段名,{@link DataField#value}作为匹配值<br/>
     * <ul>
     * <li>
     * 如果value is null,则匹配 name is null;
     * </li>
     * <li>
     * 如果value为数组或者集合,则匹配 name in (...);
     * </li>
     * <li>
     * 否则匹配 name=value
     * </li>
     * </ul>
     * 如果conditions为空(but not null),则查询全部数据
     *
     * @param tableName  表名
     * @param conditions 条件字段
     * @return
     */
    public static SqlStatement selectSql(String tableName, List<DataField> conditions) {
        SqlStatement sqlStatement = new SqlStatement("select * from").append(tableName);
        return where(sqlStatement, conditions);
    }

    /**
     * 构建insert语句
     *
     * @param entity 实体对象
     * @return
     */
    public static <T> SqlStatement insertSql(T entity) {
        DataTable<T> table = get(entity.getClass());
        List<DataField> fields = getFields(entity, table.getFieldNames(), false);
        return insertSql(table.getName(), fields);
    }


    /**
     * 构建insert语句,允许null字段
     *
     * @param tableName 表名
     * @param fields    字段列表
     * @return
     */
    public static SqlStatement insertSql(String tableName, Collection<DataField> fields) {
        assert !fields.isEmpty() : "required at least one field to insert";
        SqlStatement sqlStatement = new SqlStatement("insert into").append(tableName);
        StringBuilder nameString = new StringBuilder("(");
        StringBuilder valueString = new StringBuilder("values(");

        Iterator<DataField> iterator = fields.iterator();
        while (iterator.hasNext()) {
            DataField field = iterator.next();
            nameString.append(field.name);
            valueString.append("?");
            if (iterator.hasNext()) {
                nameString.append(SqlOperator.Comma);
                valueString.append(SqlOperator.Comma);
            }
        }

        nameString.append(")");
        valueString.append(")");
        return sqlStatement.append(nameString.toString()).append(valueString.toString(), fields.toArray(new Object[0]));
    }


    /**
     * 构建update语句<p/>
     * 如果条件字段为空,按照主键更新<p/>
     *
     * @param entity 实体对象
     * @param <T>
     * @return
     */
    public static <T> SqlStatement updateSql(T entity, String... conditionFields) {
        DataTable<T> table = get(entity.getClass());
        return updateByFields(table, entity, conditionFields.length == 0 ? table.getKeys() : conditionFields);
    }

    /**
     * 构建update语句,其中conditionFields字段列表作为更新条件 <br/>
     *
     * @param table           实体表
     * @param entity          实体对象
     * @param conditionFields 条件字段
     * @param <T>
     * @return
     */
    private static <T> SqlStatement updateByFields(DataTable<T> table, T entity, String[] conditionFields) {

        String[] updateFields = ArrayUtils.subtract(table.getFieldNames(), conditionFields, String.class);
        //条件字段
        List<DataField> conditions = getFields(entity, conditionFields, true);
        //更新字段
        List<DataField> updates = getFields(entity, updateFields, false);
        return updateSql(table.getName(), updates, conditions);
    }


    /**
     * 构建update语句,其中updateFields为待更新字段,conditionFields字段列表作为更新条件<br/>
     *
     * @param tableName       表名
     * @param updateFields    待更新字段
     * @param conditionFields 条件字段
     * @return
     */
    public static SqlStatement updateSql(String tableName, Collection<DataField> updateFields, Collection<DataField> conditionFields) {
        throwIf(updateFields.isEmpty(), "required at least one field to update");
        SqlStatement sqlStatement = new SqlStatement("update").append(tableName).append("set");
        Iterator<DataField> iterator = updateFields.iterator();
        while (iterator.hasNext()) {
            DataField field = iterator.next();
            sqlStatement.append(SqlOperator.Equal.toSQL(field.name), field);
            if (iterator.hasNext()) {
                sqlStatement.append(SqlOperator.Comma.toString());
            }
        }
        return where(sqlStatement, conditionFields);
    }


    /**
     * 构建delete语句,使用主键作为查询条件<br/>
     *
     * @param entity 实体对象
     * @param <T>
     * @return
     */
    public static <T> SqlStatement deleteByKey(T entity) {
        DataTable table = get(entity.getClass());
        return deleteSql(table.getName(), getFields(entity, table.getKeys(), true));
    }


    /**
     * 构建delete语句<p/>
     * 如果条件字段为空,使用非空属性作为查询条件<br/>
     *
     * @param entity 实体对象
     * @param <T>
     * @return
     */
    public static <T> SqlStatement deleteSql(T entity, String... conditionFields) {
        DataTable table = get(entity.getClass());
        List<DataField> dataFields = getFields(entity, conditionFields.length > 0 ? conditionFields : table.getFieldNames(), conditionFields.length > 0);
        return deleteSql(table.getName(), dataFields);
    }


    /**
     * 构建delete语句,conditionFields作为删除字段<br/>
     *
     * @param tableName
     * @param conditions
     * @return
     */
    public static SqlStatement deleteSql(String tableName, Collection<DataField> conditions) {
        return where(new SqlStatement("delete from").append(tableName), conditions);
    }


    /**
     * 组装Where条件
     *
     * @param statement
     * @param conditions
     * @return
     */
    public static SqlStatement where(SqlStatement statement, Collection<DataField> conditions) {
        return statement.append(conditions.size() > 0 ? "where" : "").append(conditions);
    }

    public static List<DataField> getFields(Object entity, String... fields) {
        return getFields(entity, fields, true);
    }


    private static List<DataField> getFields(Object entity, String[] fields, boolean includeNull) {
        DataTable table = get(entity.getClass());
        return Arrays.stream(fields)
                .map(table::getField)
                .map(f -> new DataField(f.getName(), f.getValue(entity), f.getSqlType()))
                .filter(f -> includeNull || f.value != null)
                .collect(Collectors.toList());
    }


}
