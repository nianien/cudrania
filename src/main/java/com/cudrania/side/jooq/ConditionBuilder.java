package com.cudrania.side.jooq;


import com.cudrania.core.arrays.ArrayUtils;
import com.cudrania.core.reflection.Reflections;
import com.cudrania.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
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

    private static final Pattern UPPER_PATTERN = Pattern.compile("[A-Z]");
    private SQLDialect sqlDialect = SQLDialect.DEFAULT;


    /**
     * 生成查询字段
     */
    private BiFunction<String, Class<?>, Field<?>> fieldGenerator;

    /**
     * 生成查询字段名称
     */
    private Function<String, String> nameGenerator = Function.identity();

    /**
     * 查询字段过滤
     */
    private Predicate<QueryField> filter = (f) -> true;


    /**
     * 自定义生成查询字段
     *
     * @param fieldGenerator
     * @return
     */
    protected ConditionBuilder(Function<String, String> nameGenerator, BiFunction<String, Class<?>, Field<?>> fieldGenerator) {
        if (nameGenerator != null) {
            this.nameGenerator = nameGenerator;
        } else {
            this.nameGenerator = Function.identity();
        }
        if (fieldGenerator != null) {
            this.fieldGenerator = fieldGenerator;
        } else {
            this.fieldGenerator = (name, type) -> field(name, type, sqlDialect);
        }
    }


    /**
     * 根据表定义的字段同名和下划线映射, 将存在的Java字段作为查询条件<br>
     *
     * @param table
     * @return
     */
    public static ConditionBuilder byTable(Table table) {
        return new ConditionBuilder(Function.identity(), (name, type) -> {
            Field field = table.field(name);
            if (field == null) {
                name = humpToUnderLine(name);
                field = table.field(name);
            }
            return field;
        });
    }


    /**
     * 转下划线映射
     *
     * @return
     */
    public static ConditionBuilder byUnderLine() {
        return new ConditionBuilder(ConditionBuilder::humpToUnderLine, null);
    }

    /**
     * 同名映射
     *
     * @return
     */
    public static ConditionBuilder byName() {
        return new ConditionBuilder(Function.identity(), null);
    }


    /**
     * 设置SQL方言
     *
     * @param sqlDialect
     * @return
     */
    public ConditionBuilder with(SQLDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
        return this;
    }

    /**
     * 设置查询字段的匹配操作
     *
     * @param name     指定字段名称,  驼峰自动匹配下划线模式
     * @param operator 匹配运算符,如果为null则该字段不参与查询
     * @return
     */
    public ConditionBuilder with(String name, Operator operator) {
        final String name1 = name.toLowerCase();
        final String name2 = humpToUnderLine(name).toLowerCase();
        return with(f -> f.toLowerCase().equals(name1) || f.toLowerCase().equals(name2), operator);
    }


    /**
     * 设置查询字段的匹配操作
     *
     * @param regex    字段名正则表达式
     * @param operator 匹配运算符,如果为null则该字段不参与查询
     * @return
     */
    public ConditionBuilder withRegex(String regex, Operator operator) {
        return with(f -> f.matches(regex), operator);
    }


    /**
     * 设置查询字段的匹配操作
     *
     * @param predicate 查询字段断言,断言为真,则参与查询
     * @param operator  匹配运算符,如果为null,则该字段不参与查询
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
     * @param filter 查询字段断言,断言为真,则参与查询
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
        Condition condition = DSL.noCondition();
        List<QueryField> queryFields = getQueryFields(queryBean);
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
        Object[] values = ArrayUtils.forceToArray(queryField.value);
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
     * @param queryBean
     * @return
     */
    private List<QueryField> getQueryFields(Object queryBean) {
        List<java.lang.reflect.Field> javaFields = Reflections.getFields(queryBean.getClass(), Object.class, null);
        List<QueryField> queryFields = new ArrayList<>(javaFields.size());
        for (java.lang.reflect.Field javaField : javaFields) {
            Operator operator = Operator.EQ;
            String name = javaField.getName();
            Match match = Reflections.findAnnotation(javaField, Match.class);
            if (match != null) {
                if (match.disable()) {
                    continue;
                }
                //优先使用注解定义
                if (StringUtils.isNotEmpty(match.name())) {
                    name = match.name();
                } else {
                    //否则使用命名函数
                    name = nameGenerator.apply(name);
                }
                operator = match.op();
            }
            //获取查询字段值
            Object fieldValue = Reflections.getFieldValue(queryBean, javaField);
            //字段转成数组,用于空值判断和字段类型推断
            Object[] array = ArrayUtils.forceToArray(fieldValue);
            if (array.length == 0) {
                continue;
            }
            // 根据名称和类型生成查询字段
            Field<?> field = fieldGenerator.apply(name, array[0].getClass());
            if (field == null) {
                continue;
            }
            queryFields.add(new QueryField(field, array[0].getClass(), fieldValue, operator));
        }
        return queryFields;
    }


    /**
     * 驼峰转下划线
     *
     * @param str
     * @return
     */
    private static String humpToUnderLine(String str) {
        Matcher matcher = UPPER_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * 创建字段
     *
     * @param name
     * @param type
     * @param sqlDialect
     * @return
     */
    private static Field<?> field(String name, Class<?> type, SQLDialect sqlDialect) {
        if (type == Date.class) {
            type = Timestamp.class;
        }
        try {
            return DSL.field(name, DefaultDataType.getDataType(sqlDialect, type));
        } catch (Exception e) {
            return DSL.field(name, DefaultDataType.getDataType(sqlDialect, Object.class));
        }
    }


    /**
     * 查询字段
     */
    @Data
    @AllArgsConstructor
    public static class QueryField {
        /**
         * 生成的查询字段
         */
        private final Field field;
        /**
         * Java字段类型或集合元素类型
         */
        private final Class rawType;
        /**
         * Java字段值
         */
        private final Object value;
        /**
         * 匹配操作符
         */
        private Operator operator;

    }


}
