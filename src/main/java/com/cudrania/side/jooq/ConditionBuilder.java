package com.cudrania.side.jooq;


import com.cudrania.core.collection.CollectionUtils;
import com.cudrania.core.reflection.Reflections;
import com.cudrania.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
     * 用于生成字段的函数
     */
    private Function<String, Field> fieldGenerator;
    /**
     * 用于过滤字段的函数
     */
    private Predicate<QueryField> fieldFilter;


    /**
     * 对象字段名作为数据库查询字段, 自动转换为下划线形式
     *
     * @return
     */
    public ConditionBuilder() {
        this(true);
    }

    /**
     * 对象字段名作为数据库查询字段
     *
     * @param underlineStyle 是否自动转换为下划线形式
     */
    public ConditionBuilder(boolean underlineStyle) {
        this(underlineStyle ? byUnderLine() : byName());
    }

    /**
     * 数据库表字段作为查询字段
     *
     * @param table
     * @return
     */
    public ConditionBuilder(Table table) {
        this(byTable(table));
    }


    /**
     * 自定义生成查询字段
     *
     * @param fieldGenerator
     * @return
     */
    public ConditionBuilder(Function<String, Field> fieldGenerator) {
        this.fieldGenerator = fieldGenerator;
    }

    /**
     * 过滤查询字段
     *
     * @param fieldFilter
     * @return
     */
    public ConditionBuilder filter(Predicate<QueryField> fieldFilter) {
        this.fieldFilter = fieldFilter;
        return this;
    }


    /**
     * 使用默认转换逻辑
     *
     * @return
     */
    public Condition build(Object queryBean) {
        return toCondition(queryBean, fieldGenerator, fieldFilter);
    }


    /**
     * 自定义函数生成匹配条件
     *
     * @param queryBean
     * @param fieldGenerator
     * @param fieldFilter
     * @return
     */
    public static Condition toCondition(Object queryBean, Function<String, Field> fieldGenerator,
                                        Predicate<QueryField> fieldFilter) {
        Condition condition = DSL.trueCondition();
        List<QueryField> queryFields = getQueryFields(queryBean, fieldFilter);
        for (QueryField info : queryFields) {
            Field field = fieldGenerator.apply(info.name);
            if (field == null) {
                continue;
            }
            Condition subCondition = singleCondition(info, field);
            if (subCondition != null) {
                condition = condition.and(subCondition);
            }
        }
        return condition;
    }


    /**
     * 基于单个字段创建匹配条件
     *
     * @param info
     * @param field
     * @return
     */
    private static Condition singleCondition(QueryField info, Field field) {
        Object[] values = info.asList().toArray(new Object[0]);
        if (values.length == 0) {
            return null;
        }
        Object value = values[0];
        switch (info.operator) {
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
                    throw new IllegalArgumentException("size of field[" + info.name + "] must be 2!");
                }
            case NOT_BETWEEN:
                if (values.length == 2) {
                    return field.notBetween(values[0], values[1]);
                } else {
                    throw new IllegalArgumentException("size of field[" + info.name + "] must be 2!");
                }
            default:
                return null;
        }
    }

    /**
     * 获取查询字段
     *
     * @param query
     * @param fieldFilter
     * @return
     */
    private static List<QueryField> getQueryFields(Object query, Predicate<QueryField> fieldFilter) {
        List<java.lang.reflect.Field> fields = Reflections.getFields(query.getClass(), Object.class, null);
        List<QueryField> queryFields = new ArrayList<>(fields.size());
        for (java.lang.reflect.Field field : fields) {
            Operator operator = Operator.EQ;
            String name = field.getName();
            Match match = Reflections.findAnnotation(field, Match.class);
            if (match != null) {
                if (match.disable()) {
                    continue;
                }
                if (StringUtils.isNotEmpty(match.name())) {
                    name = match.name();
                }
                operator = match.op();
            }
            Object value = Reflections.getFieldValue(query, field);
            QueryField queryField = new QueryField(field, name, value, operator);
            if (fieldFilter == null || fieldFilter.test(queryField)) {
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
    @Getter
    @AllArgsConstructor
    public static class QueryField {
        java.lang.reflect.Field field;
        String name;
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
