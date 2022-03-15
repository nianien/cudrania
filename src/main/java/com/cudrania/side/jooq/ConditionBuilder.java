package com.cudrania.side.jooq;


import com.cudrania.core.arrays.ArrayUtils;
import com.cudrania.core.functions.Fn;
import com.cudrania.core.reflection.BeanProperty;
import com.cudrania.core.reflection.Reflections;
import com.cudrania.core.utils.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.TableLike;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.cudrania.core.utils.StringUtils.decapitalize;
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
    public static ConditionBuilder byTable(TableLike table) {
        return new ConditionBuilder(Function.identity(), (property, type) -> {
            Field field = table.field(property);
            if (field == null) {
                property = underscoreCase(property);
                field = table.field(property);
            }
            return field;
        });
    }


    /**
     * 同名映射,自动转下划线
     *
     * @return
     */
    public static ConditionBuilder byName() {
        return new ConditionBuilder(StringUtils::underscoreCase);
    }


    /**
     * 设置匹配字段的查询操作
     *
     * @param name     指定字段名称, 同时匹配下划线模式
     * @param operator 查询操作,如果为null则该字段不参与查询
     * @return
     */
    public ConditionBuilder match(String name, Operator operator) {
        Set<String> set = new HashSet<>(Arrays.asList(name.toLowerCase(), underscoreCase(name).toLowerCase()));
        return filter(f -> {
            String fieldName = f.getField().getName();
            if (set.contains(fieldName) || fieldName.matches(name)) {
                f.setOperator(operator);
            }
            return true;
        });

    }

    /**
     * 设置匹配字段的查询操作
     *
     * @param getter   指定Getter方法, 同时匹配下划线模式
     * @param operator 查询操作,如果为null则该字段不参与查询
     * @return
     */
    public <P, R> ConditionBuilder match(Fn.Function<P, R> getter, Operator operator) {
        String name = getter.name();
        int index = name.startsWith("get") || name.startsWith("set") ? 3 : name.startsWith("is") ? 2 : 0;
        String propertyName = decapitalize(name.substring(index));
        return filter(f -> {
            if (f.property.getName().equals(propertyName)) {
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
    public ConditionBuilder filter(Predicate<QueryField> filter) {
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
                    || queryField.operator == Operator.NONE) {
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
        List<BeanProperty> beanProperties = Reflections.beanProperties(queryBean.getClass());
        List<QueryField> queryFields = new ArrayList<>(beanProperties.size());
        for (BeanProperty property : beanProperties) {
            QueryField queryField = createQueryField(property, queryBean);
            if (queryField != null) {
                queryFields.add(queryField);
            }
        }
        return queryFields;
    }


    /**
     * 创建查询字段
     *
     * @param property
     * @param queryBean
     * @return
     */
    private QueryField createQueryField(BeanProperty property, Object queryBean) {
        Operator operator = Operator.EQ;
        String name = property.getName();
        Match[] matches = property.getAnnotations(Match.class);
        if (matches.length > 0) {
            Match match = matches[0];
            operator = match.value();
            if (operator == Operator.NONE) {
                return null;
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
        Object fieldValue = property.getValue(queryBean);
        //字段转成数组,用于空值判断和字段类型推断
        Object[] array = ArrayUtils.forceToArray(fieldValue);
        if (array.length == 0) {
            return null;
        }
        // 根据名称和类型生成查询字段
        Field<?> field = fieldGenerator.apply(name, array[0].getClass());
        if (field == null) {
            return null;
        }
        return new QueryField(field, array[0].getClass(), fieldValue, operator, property);
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
    @Getter
    @Setter(AccessLevel.PRIVATE)
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

        @Getter(AccessLevel.PRIVATE)
        private BeanProperty property;

    }


}
