package com.cudrania.side.jooq;


import com.cudrania.core.collection.CollectionUtils;
import com.cudrania.core.reflection.Reflections;
import com.cudrania.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 匹配条件构造器<br/>
 * scm.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @see Match
 */
public class ConditionBuilder {

    /**
     * 条件查询字段生成器
     */
    private Function<String, Field> fieldGenerator;
    /**
     * 条件查询字段过滤
     */
    private Predicate<QueryField> filter = (f) -> true;


    /**
     * 对象字段名作为条件查询字段, 自动转换为下划线形式
     *
     * @return
     */
    public ConditionBuilder() {
        this(true);
    }

    /**
     * bean对象字段名作为条件查询字段
     *
     * @param underlineStyle 是否自动转换为下划线形式
     */
    public ConditionBuilder(boolean underlineStyle) {
        this(underlineStyle ? byUnderLine() : byName());
    }

    /**
     * 数据库表字段作为条件查询字段
     *
     * @param table
     * @return
     */
    public ConditionBuilder(Table table) {
        this(byTable(table));
    }


    /**
     * 自定义生成条件查询字段
     *
     * @param fieldGenerator
     * @return
     */
    public ConditionBuilder(Function<String, Field> fieldGenerator) {
        this.fieldGenerator = fieldGenerator;
    }


    /**
     * 设置条件查询字段的匹配操作
     *
     * @param name     指定字段名称,  驼峰自动匹配下划线模式
     * @param operator 匹配运算符,如果为null则该字段不参与条件查询
     * @return
     */
    public ConditionBuilder with(String name, Operator operator) {
        return with(new HashSet<>(Arrays.asList(name.toLowerCase(), humpToUnderLine(name).toLowerCase()))::contains, operator);
    }


    /**
     * 设置条件查询字段的匹配操作
     *
     * @param predicate 条件查询字段断言,断言为真,则参与条件查询
     * @param operator  匹配运算符,如果为null,则该字段不参与条件查询
     * @return
     */
    public ConditionBuilder with(Predicate<String> predicate, Operator operator) {
        this.filter = this.filter.and(
                f -> {
                    if (predicate.test(f.field.getName())) {
                        if (operator == null) {
                            return false;
                        }
                        f.setOperator(operator);
                    }
                    return true;
                }
        );
        return this;
    }


    /**
     * 过滤查询字段
     *
     * @param filter 条件查询字段断言,断言为真,则参与条件查询
     * @return
     */
    public ConditionBuilder with(Predicate<QueryField> filter) {
        this.filter = this.filter.and(filter);
        return this;
    }


    /**
     * 根据查询对象内容生成查询条件
     *
     * @param queryBean 查询对象, 字段值作为查询条件
     * @return
     */
    public Condition build(Object queryBean) {
        return toCondition(queryBean, fieldGenerator, filter);
    }


    /**
     * 根据查询对象内容生成查询条件
     *
     * @param queryBean      查询对象, 字段值作为查询条件
     * @param fieldGenerator 查询字段生成器
     * @param filter         查询字段过滤器
     * @return
     */
    public static Condition toCondition(Object queryBean, Function<String, Field> fieldGenerator,
                                        Predicate<QueryField> filter) {
        Condition condition = DSL.noCondition();
        List<QueryField> queryFields = getQueryFields(queryBean, fieldGenerator);
        for (QueryField queryField : queryFields) {
            Field field = queryField.field;
            if (field == null || !filter.test(queryField)) {
                continue;
            }
            Condition subCondition = singleCondition(queryField);
            if (subCondition != null) {
                condition = condition.and(subCondition);
            }
        }
        return condition;
    }


    /**
     * 基于单个字段创建匹配条件
     *
     * @param queryField
     * @return
     */
    private static Condition singleCondition(QueryField queryField) {
        Object[] values = queryField.asList().toArray(new Object[0]);
        if (values.length == 0) {
            return null;
        }
        Object value = values[0];
        Field field = queryField.field;
        switch (queryField.operator) {
            case EQ:
                if (values.length > 1) {
                    return field.in(values);
                }
                return field.eq(value);
            case NE:
                return field.ne(value);
            case GT:
                return field.gt(value);
            case GE:
                return field.ge(value);
            case LT:
                return field.lt(value);
            case LE:
                return field.le(value);
            case LIKE:
                return field.contains(value);
            case NOT_LIKE:
                return field.notContains(value);
            case IN:
                return field.in(values);
            case NOT_IN:
                return field.notIn(values);
            case BETWEEN:
                if (values.length == 2) {
                    return field.between(values[0], values[1]);
                }
                if (values.length == 1) {
                    return field.ge(values[0]);
                } else {
                    throw new IllegalArgumentException("size of field[" + field.getName() + "] must be 2!");
                }
            case NOT_BETWEEN:
                if (values.length == 2) {
                    return field.notBetween(values[0], values[1]);
                } else {
                    throw new IllegalArgumentException("size of field[" + field.getName() + "] must be 2!");
                }
            default:
                return null;
        }
    }

    /**
     * 获取查询字段
     *
     * @param query
     * @param fieldGenerator
     * @return
     */
    private static List<QueryField> getQueryFields(Object query, Function<String, Field> fieldGenerator) {
        List<java.lang.reflect.Field> javaFields = Reflections.getFields(query.getClass(), Object.class, null);
        List<QueryField> queryFields = new ArrayList<>(javaFields.size());
        for (java.lang.reflect.Field javaField : javaFields) {
            Operator operator = Operator.EQ;
            String name = javaField.getName();
            Match match = Reflections.findAnnotation(javaField, Match.class);
            if (match != null) {
                if (match.disable()) {
                    continue;
                }
                if (StringUtils.isNotEmpty(match.name())) {
                    name = match.name();
                }
                operator = match.op();
            }
            QueryField queryField = new QueryField(fieldGenerator.apply(name), Reflections.getFieldValue(query, javaField), operator);
            if (queryField.field != null && queryField.value != null) {
                queryFields.add(queryField);
            }
        }
        return queryFields;
    }

    private static Pattern STR_PATTERN = Pattern.compile("[A-Z]");


    /**
     * 同名映射
     *
     * @return
     */
    public static Function<String, Field> byName() {
        return name -> DSL.field(name);
    }

    /**
     * 转下划线映射
     *
     * @return
     */
    public static Function<String, Field> byUnderLine() {
        return name -> DSL.field(humpToUnderLine(name));
    }

    /**
     * 表字段映射,自动适配下划线风格
     *
     * @param table
     * @return
     */
    public static Function<String, Field> byTable(Table table) {
        return (name) -> findField(table, name);
    }


    /**
     * 驼峰转下划线
     *
     * @param str
     * @return
     */
    private static String humpToUnderLine(String str) {
        Matcher matcher = STR_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 根据对象字段名查询表字段,自动识别下划线风格
     *
     * @param table 数据库表
     * @param name  对象字段
     * @return 数据库表字段
     */
    private static Field findField(Table table, String name) {
        Field field = table.field(name);
        if (field == null) {
            name = humpToUnderLine(name);
            field = table.field(name);
        }
        return field;
    }


    /**
     * 查询字段
     */
    @Data
    @AllArgsConstructor
    public static class QueryField {
        Field field;
        Object value;
        Operator operator;

        /**
         * 非空值转集合
         *
         * @return
         */
        Collection asList() {
            if (value == null ||
                    value instanceof String && StringUtils.isEmpty((String) value)) {
                return Collections.EMPTY_LIST;
            }
            if (field.getType().isArray()) {
                return CollectionUtils.arrayToList(value);
            } else if (value instanceof Collection) {
                return (Collection) value;
            } else {
                return Arrays.asList(value);
            }
        }
    }


}
