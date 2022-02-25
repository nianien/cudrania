package com.cudrania.idea.jdbc.sql;

import com.cudrania.idea.jdbc.table.DataField;
import com.cudrania.idea.jdbc.table.DataTable;
import com.cudrania.idea.jdbc.table.DataTableFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
     * 构建select语句<br/>
     * 如果未指定conditionFields,则默认使用实体对象的not null字段<br/>
     * 如果conditionFields为null,则查询全部数据
     *
     * @param tableName       表名
     * @param entity          实体对象
     * @param conditionFields 条件字段
     * @param <T>
     * @return
     */
    public static <T> SqlStatement selectSql(String tableName, T entity, String... conditionFields) {
        DataTable<T> table = DataTableFactory.get(tableName, entity.getClass());
        return selectSql(table, entity, conditionFields);
    }

    /**
     * 构建select语句<br/>
     * 如果未指定conditionFields,则默认使用实体对象的not null字段<br/>
     * 如果conditionFields为null,则查询全部数据
     *
     * @param entity          实体对象
     * @param conditionFields 条件字段
     * @param <T>
     * @return
     */
    public static <T> SqlStatement selectSql(T entity, String... conditionFields) {
        DataTable<T> table = DataTableFactory.get(entity.getClass());
        return selectSql(table, entity, conditionFields);
    }


    /**
     * 构建select语句<br/>
     * 如果未指定conditionFields,则默认使用实体对象的not null字段<br/>
     * 如果conditionFields为null,则查询全部数据
     *
     * @param table           实体表
     * @param entity          实体对象
     * @param conditionFields 条件字段
     * @param <T>
     * @return
     */
    private static <T> SqlStatement selectSql(DataTable<T> table, T entity, String... conditionFields) {
        if (conditionFields == null) {
            return selectSql(table.getName(), new ArrayList<DataField>());
        }
        if (conditionFields.length == 0) {
            return selectSql(table.getName(), notNull(table.getFields(entity)));
        }
        return selectSql(table.getName(), table.getFields(entity, conditionFields));
    }


    /**
     * 构建select语句.查询条件conditions是一个Map对象,其中key作为字段名,value作为匹配值<br/>
     * <ul>
     * <li>
     * 如果value is null,则匹配 key is null;
     * </li>
     * <li>
     * 如果value为数组或者集合,则匹配 key in (...);
     * </li>
     * <li>
     * 否则匹配 key=value
     * </li>
     * </ul>
     * 如果conditions为空(but not null),则查询全部数据
     *
     * @param tableName  表名
     * @param conditions 查询条件
     * @return
     */
    public static SqlStatement selectSql(String tableName, Map<String, ?> conditions) {
        return selectSql(tableName, getDataFields(conditions));
    }

    /**
     * 构建select语句.查询条件conditions是一个DataField列表,其中DataField.name作为字段名,DataField.value作为匹配值<br/>
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
        return whereSql(sqlStatement, conditions);
    }


    /**
     * 构建insert语句,忽略实体对象的null属性
     *
     * @param tableName 表名
     * @param entity    实体对象
     * @return
     */
    public static <T> SqlStatement insertSql(String tableName, T entity) {
        DataTable<T> table = DataTableFactory.get(tableName, entity.getClass());
        return insertSql(table, entity);
    }

    /**
     * 构建insert语句,忽略实体对象的null属性
     *
     * @param entity 实体对象
     * @return
     */
    public static <T> SqlStatement insertSql(T entity) {
        DataTable<T> table = DataTableFactory.get(entity.getClass());
        return insertSql(table, entity);
    }

    /**
     * 构建insert语句,忽略值实体对象的null属性
     *
     * @param table  实体表
     * @param entity 实体对象
     * @param <T>
     * @return
     */
    private static <T> SqlStatement insertSql(DataTable<T> table, T entity) {
        return insertSql(table.getName(), notNull(table.getFields(entity)));
    }


    /**
     * 构建insert语句,允许null字段
     *
     * @param tableName 表名
     * @param fields    字段列表
     * @return
     */
    public static SqlStatement insertSql(String tableName, Map<String, ?> fields) {
        return insertSql(tableName, getDataFields(fields));
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
                nameString.append(SqlOperator.Comma.toString());
                valueString.append(SqlOperator.Comma.toString());
            }
        }

        nameString.append(")");
        valueString.append(")");
        return sqlStatement.append(nameString.toString()).append(valueString.toString(), fields.toArray(new Object[0]));
    }

    /**
     * 构建update语句.其中conditionFields字段列表作为更新条件 <br/>
     * 如果conditionFields未提供,则将按键值更新<br/>
     * 如果conditionFields为null,则将更新全部数据<br/>
     * 注: 这里null字段会被更新
     *
     * @param tableName       表名
     * @param entity          实体对象
     * @param conditionFields 条件字段
     * @param <T>
     * @return
     */
    public static <T> SqlStatement updateSql(String tableName, T entity, String... conditionFields) {
        DataTable<T> table = DataTableFactory.get(tableName, entity.getClass());
        return updateSql(table, entity, conditionFields);
    }


    /**
     * 构建update语句.其中conditionFields字段列表作为更新条件 <br/>
     * 如果conditionFields未提供,则将按键值更新<br/>
     * 如果conditionFields为null,则将更新全部数据<br/>
     * 注: 这里null字段会被更新
     *
     * @param entity          实体对象
     * @param conditionFields 条件字段
     * @param <T>
     * @return
     */
    public static <T> SqlStatement updateSql(T entity, String... conditionFields) {
        DataTable<T> table = DataTableFactory.get(entity.getClass());
        return updateSql(table, entity, conditionFields);
    }

    /**
     * 构建update语句.其中conditionFields字段列表作为更新条件 <br/>
     * 如果conditionFields未提供,则将按键值更新<br/>
     * 如果conditionFields为null,则将更新全部数据<br/>
     * 注: 这里null字段会被更新
     *
     * @param table           实体表
     * @param entity          实体对象
     * @param conditionFields 条件字段
     * @param <T>
     * @return
     */
    private static <T> SqlStatement updateSql(DataTable<T> table, T entity, String... conditionFields) {
        if (conditionFields == null) {
            return updateSql(table.getName(), table.getFields(entity), new ArrayList<DataField>());
        }
        if (conditionFields.length == 0) {
            return updateSql(table.getName(), table.getFields(entity), Arrays.asList(table.idField(entity)));
        }
        //条件字段名
        Set<String> conditionSet = new HashSet<String>();
        for (String fieldName : conditionFields) {
            conditionSet.add(table.getFieldName(fieldName));
        }

        //更新字段
        List<DataField> fields = table.getFields(entity);
        List<DataField> conditions = new ArrayList<DataField>();

        Iterator<DataField> iterator = fields.iterator();
        //条件字段
        while (iterator.hasNext()) {
            DataField field = iterator.next();
            if (conditionSet.contains(field.name)) {
                conditions.add(field);
                iterator.remove();
            }
        }
        return updateSql(table.getName(), fields, conditions);
    }

    /**
     * 构建update语句,其中updateFields为待更新字段,conditionFields字段列表作为更新条件<br/>
     * 如果conditions为空(but not null),则将更新全部数据<br/>
     * 注: 这里null字段会被更新
     *
     * @param tableName       表名
     * @param updateFields    待更新字段
     * @param conditionFields 条件字段
     * @return
     */
    public static SqlStatement updateSql(String tableName, Map<String, ?> updateFields, Map<String, ?> conditionFields) {
        return updateSql(tableName, getDataFields(updateFields), getDataFields(conditionFields));
    }

    /**
     * 构建update语句,其中updateFields为待更新字段,conditionFields字段列表作为更新条件<br/>
     * 如果conditions为空(but not null),则将更新全部数据<br/>
     * 注: 这里null字段会被更新
     *
     * @param tableName       表名
     * @param updateFields    待更新字段
     * @param conditionFields 条件字段
     * @return
     */
    public static SqlStatement updateSql(String tableName, Collection<DataField> updateFields, Collection<DataField> conditionFields) {
        assert !updateFields.isEmpty() : "required at least one field to update";
        SqlStatement sqlStatement = new SqlStatement("update").append(tableName).append("set");
        Iterator<DataField> iterator = updateFields.iterator();
        while (iterator.hasNext()) {
            DataField field = iterator.next();
            sqlStatement.append(SqlOperator.Equal.toSQL(field.name), field);
            if (iterator.hasNext()) {
                sqlStatement.append(SqlOperator.Comma.toString());
            }
        }
        return whereSql(sqlStatement, conditionFields);
    }

    /**
     * 构建delete语句,conditionFields作为删除字段<br/>
     * 如果未指定conditionFields,则默认使用实体对象的not null字段<br/>
     * 如果conditionFields为null,则删除全部数据
     *
     * @param tableName       表名
     * @param entity          实体对象
     * @param conditionFields 条件字段
     * @param <T>
     * @return
     */
    public static <T> SqlStatement deleteSql(String tableName, T entity, String... conditionFields) {
        DataTable table = DataTableFactory.get(tableName, entity.getClass());
        return deleteSql(table, entity, conditionFields);
    }

    /**
     * 构建delete语句,conditionFields作为删除字段<br/>
     * 如果未指定conditionFields,则默认使用实体对象的not null字段<br/>
     * 如果conditionFields为null,则删除全部数据
     *
     * @param entity          实体对象
     * @param conditionFields 条件字段
     * @param <T>
     * @return
     */
    public static <T> SqlStatement deleteSql(T entity, String... conditionFields) {
        DataTable table = DataTableFactory.get(entity.getClass());
        return deleteSql(table, entity, conditionFields);
    }

    /**
     * 构建delete语句,conditionFields作为删除字段<br/>
     * 如果未指定conditionFields,则默认使用实体对象的not null字段<br/>
     * 如果conditionFields为null,则删除全部数据
     *
     * @param table           实体表
     * @param entity          实体对象
     * @param conditionFields 条件字段
     * @param <T>
     * @return
     */
    private static <T> SqlStatement deleteSql(DataTable<T> table, T entity, String... conditionFields) {
        if (conditionFields == null) {
            return deleteSql(table.getName(), new ArrayList<>());
        }
        if (conditionFields.length == 0) {
            return deleteSql(table.getName(), notNull(table.getFields(entity)));
        }
        return deleteSql(table.getName(), table.getFields(entity, conditionFields));
    }

    /**
     * 构建delete语句,conditionFields作为删除字段<br/>
     * 如果conditionFields为空(but not null),则删除全部数据
     *
     * @param tableName
     * @param conditions
     * @return
     */
    public static SqlStatement deleteSql(String tableName, Map<String, ?> conditions) {
        return deleteSql(tableName, getDataFields(conditions));
    }

    /**
     * 构建delete语句,conditionFields作为删除字段<br/>
     * 如果conditionFields为空(but not null),则删除全部数据
     *
     * @param tableName
     * @param conditions
     * @return
     */
    public static SqlStatement deleteSql(String tableName, Collection<DataField> conditions) {
        return whereSql(new SqlStatement("delete from").append(tableName), conditions);
    }


    /**
     * 追加where语句
     *
     * @param sqlStatement
     * @param entity
     * @param conditions   条件字段
     * @param <T>
     * @return
     */
    public static <T> SqlStatement whereSql(SqlStatement sqlStatement, T entity, String... conditions) {
        DataTable table = DataTableFactory.get(entity.getClass());
        return whereSql(sqlStatement, table.getFields(entity, conditions));
    }

    /**
     * 追加where语句
     *
     * @param sqlStatement
     * @param conditions   条件字段
     * @return
     */
    public static SqlStatement whereSql(SqlStatement sqlStatement, Map<String, ?> conditions) {
        return whereSql(sqlStatement, getDataFields(conditions));
    }

    /**
     * 构建where条件,conditions作为条件字段可以为null
     *
     * @param sqlStatement
     * @param conditions
     * @return
     */
    private static SqlStatement whereSql(SqlStatement sqlStatement, Collection<DataField> conditions) {
        if (conditions.isEmpty())
            return sqlStatement;
        sqlStatement.append("where");
        Iterator<DataField> iterator = conditions.iterator();
        while (iterator.hasNext()) {
            DataField field = iterator.next();
            String name = field.name;
            Object value = field.value;
            if (value != null) {
                if (value.getClass().isArray() || value instanceof Collection) {
                    sqlStatement.append(SqlOperator.In.toSQL(name), field);
                } else {
                    sqlStatement.append(SqlOperator.Equal.toSQL(name), field);
                }
            } else {
                sqlStatement.append(SqlOperator.IsNull.toSQL(name));
            }
            if (iterator.hasNext()) {
                sqlStatement.append(SqlOperator.And.toString());
            }
        }
        return sqlStatement;
    }

    /**
     * 根据Map对象构建字段列表
     *
     * @param fields
     * @return
     */
    private static List<DataField> getDataFields(Map<String, ?> fields) {
        List<DataField> dataFields = new ArrayList<DataField>();
        for (Entry<String, ?> entry : fields.entrySet()) {
            dataFields.add(new DataField(entry.getKey(), entry.getValue()));
        }
        return dataFields;
    }

    /**
     * 移除null字段
     *
     * @param fields
     * @return
     */
    private static List<DataField> notNull(List<DataField> fields) {
        Iterator<DataField> iterator = fields.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().value == null) {
                iterator.remove();
            }
        }
        return fields;
    }

}
