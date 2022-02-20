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
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.cudrania.core.utils.StringUtils.underscoreCase;

/**
 * 匹配条件构造器<br/>
 * scm.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 *
 * @see Match
 */
public class ConditionBuilder {

    private SQLDialect sqlDialect = SQLDialect.DEFAULT;


    /**
     * 生成查询字段
     */
    private BiFunction<String, Class<?>, Field<?>> fieldGenerator;

    /**
     * 生成查询字段名称
     */
    private Function<String, String> nameGenerator;

    /**
     * 查询字段过滤
     */
    private Predicate<QueryField> filter = (f) -> true;


    /**
     * 自定义生成查询字段
     *
     * @param nameGenerator 命名函数
     */
    public ConditionBuilder(Function<String, String> nameGenerator) {
        this(nameGenerator, null);
    }

    /**
     * 自定义生成查询字段
     *
     * @param nameGenerator  命名函数
     * @param fieldGenerator 字段生成器
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
                name = underscoreCase(name);
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
        return new ConditionBuilder(StringUtils::underscoreCase);
    }

    /**
     * 同名映射
     *
     * @return
     */
    public static ConditionBuilder byName() {
        return new ConditionBuilder(Function.identity());
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
     * 设置匹配字段的查询操作
     *
     * @param name     指定字段名称, 同时匹配下划线模式
     * @param operator 查询操作,如果为null则该字段不参与查询
     * @return
     */
    public ConditionBuilder with(String name, Operator operator) {
        Set<String> set = new HashSet<>(Arrays.asList(name.toLowerCase(), underscoreCase(name).toLowerCase()));
        return with(f -> set.contains(f.toLowerCase()), operator);
    }


    /**
     * 设置匹配字段的查询操作
     *
     * @param regex    字段名匹配的正则表达式
     * @param operator 查询操作,如果为null则该字段不参与查询
     * @return
     */
    public ConditionBuilder withRegex(String regex, Operator operator) {
        return with(f -> f.matches(regex), operator);
    }

    /**
     * 设置匹配字段的查询操作
     *
     * @param predicate 字段匹配函数, 匹配时设置查询操作
     * @param operator  查询操作,如果为null则该字段不参与查询
     * @return
     */
    public ConditionBuilder with(BiPredicate<String, Object> predicate, Operator operator) {
        return with(f -> {
            if (predicate.test(f.field.getName(), f.value)) {
                f.setOperator(operator);
            }
            return true;
        });
    }

    /**
     * 设置匹配字段的查询操作
     *
     * @param predicate 字段匹配函数, 匹配时设置查询操作
     * @param operator  查询操作,如果为null则字段不参与查询
     * @return
     */
    public ConditionBuilder with(Predicate<String> predicate, Operator operator) {
        return with(f -> {
            if (predicate.test(f.field.getName())) {
                f.setOperator(operator);
            }
            return true;
        });
    }


    /**
     * 过滤查询字段
     *
     * @param filter 过滤函数,判断结果为真时作为查询条件,可同时设置查询操作,如果查询操作为null则不参与查询
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
            if (!filter.test(queryField)
                    || queryField.operator == null
                    || queryField.operator == Operator.NOP) {
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
                operator = match.value();
                if (operator == Operator.NOP) {
                    continue;
                }
                //优先使用注解定义
                if (StringUtils.isNotEmpty(match.name())) {
                    name = match.name();
                } else {
                    //否则使用命名函数
                    name = nameGenerator.apply(name);
                }
            } else {
                //否则使用命名函数
                name = nameGenerator.apply(name);
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
