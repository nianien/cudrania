package com.cudrania.test.jooq;

import com.cudrania.side.jooq.ConditionBuilder;
import com.cudrania.side.jooq.FluentCondition;
import com.cudrania.side.jooq.Operator;
import org.jooq.*;
import org.jooq.conf.RenderNameStyle;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cudrania.core.functions.Params.gt;
import static com.cudrania.core.functions.Params.notEmpty;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * scm.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
public class ConditionBuilderTest {

    static class ListTable implements TableLike {
        private List<Field<?>> fields;

        public ListTable(List<Field<?>> fields) {
            this.fields = fields;
        }

        @Override
        public Row fieldsRow() {
            return null;
        }

        @Override
        public Stream<Field<?>> fieldStream() {
            return fields.stream();
        }

        @Override
        public Field<?> field(String name) {
            return fields.stream().filter(f -> f.getName().equals(name)).findAny().orElse(null);
        }

        @Override
        public Field<?> field(Name name) {
            return fields.stream().filter(f -> f.getName().equals(name.toString())).findAny().orElse(null);
        }

        @Override
        public Field<?> field(int index) {
            return fields.get(index);
        }

        @Override
        public Field<?>[] fields() {
            return fields.stream().toArray(n -> new Field[n]);
        }

        @Override
        public Field<?>[] fields(String... fieldNames) {
            Set<String> set = Arrays.asList(fieldNames).stream().collect(Collectors.toSet());
            return fields.stream().filter(f -> set.contains(f.getName())).toArray(n -> new Field[n]);
        }

        @Override
        public Field<?>[] fields(Name... fieldNames) {
            Set<String> set = Arrays.asList(fieldNames).stream().map(Name::toString).collect(Collectors.toSet());
            return fields.stream().filter(f -> set.contains(f.getName())).toArray(n -> new Field[n]);
        }

        @Override
        public Field<?>[] fields(int... fieldIndexes) {
            List<Field> list = new ArrayList<>();
            for (int fieldIndex : fieldIndexes) {
                list.add(fields.get(fieldIndex));
            }
            return list.stream().toArray(n -> new Field[n]);
        }

        @Override
        public Table asTable() {
            return null;
        }

        @Override
        public Table asTable(String alias) {
            return null;
        }

        @Override
        public Table asTable(String alias, String... fieldAliases) {
            return null;
        }

        @Override
        public Table asTable(String alias, BiFunction aliasFunction) {
            return null;
        }

        @Override
        public Table asTable(String alias, Function aliasFunction) {
            return null;
        }

        @Override
        public Field<?>[] fields(Field[] fields) {
            return fields;
        }

        @Override
        public Field field(int index, DataType dataType) {
            return field(index);
        }

        @Override
        public Field field(int index, Class type) {
            return field(index);
        }

        @Override
        public Field field(Name name, DataType dataType) {
            return field(name);
        }

        @Override
        public Field field(Name name, Class type) {
            return field(name);
        }

        @Override
        public Field field(String name, DataType dataType) {
            return field(name);
        }

        @Override
        public Field field(String name, Class type) {
            return field(name);
        }

        @Override
        public Field field(Field field) {
            return field;
        }
    }

    static class UserTable {
        private static final Field<Long> ID = DSL.field("id", Long.class);
        private static final Field<String> NAME = DSL.field("name", String.class);
        private static final Field<Integer> TYPE = DSL.field("type", Integer.class);
    }

    private static DSLContext dslContext;

    @BeforeAll
    public static void setup() {
        DefaultConfiguration config = new DefaultConfiguration();
        config.setSQLDialect(SQLDialect.MYSQL);
        config.settings()
                //shard-sphere不支持schema
                .withRenderSchema(false)
                .withRenderNameStyle(RenderNameStyle.AS_IS);
        dslContext = DSL.using(config);
    }


    @Test
    public void testFluent() {
        long id = 100;
        String name = "jack";
        List<Integer> types = Arrays.asList(1001, 1002, 1003, 1004);
        Condition condition = FluentCondition.and()
                .when(gt(id, 10), UserTable.ID)
                .when(notEmpty(types)
                        .then(ArrayList::new)
                        .then(e -> {
                            e.remove((Object) 1004);
                            return e;
                        }), UserTable.TYPE::notIn)
                .when(notEmpty(name), UserTable.NAME, (f, p) -> f.ne(p))
                .when(notEmpty(name), p -> UserTable.NAME.ne(p))
                .get();

        System.out.println(dslContext.renderInlined(condition));
        System.out.println(dslContext.renderNamedOrInlinedParams(condition));
        System.out.println("==========================");
        System.out.println(dslContext.render(condition));
        System.out.println(dslContext.extractBindValues(condition));
        System.out.println("==========================");
        System.out.println(dslContext.renderNamedParams(condition));
        System.out.println(dslContext.extractParams(condition));
    }

    static ListTable GoodsQueryTable = new ListTable(Arrays.asList(
            DSL.field("out_submit_time", java.sql.Date.class),
            DSL.field("src_store_name", String.class),
            DSL.field("industry_name", Integer.class),
            DSL.field("industry_id", Integer.class),
            DSL.field("id", Integer.class),
            DSL.field("price", BigDecimal.class),
            DSL.field("biz_type", Integer.class)
    ));


    @Test
    public void testBuilder() {

        GoodsQuery query = new GoodsQuery();
        query.setSubmitTimeBegin(new Date());
        query.setSubmitTimeEnd(new Date());
        query.setSrcStoreName("abc");
        query.setIndustryName("ddd");
        query.setIndustryId(-100L);
        query.setId(100L);
        query.setPrice(new BigDecimal(1111));
        Condition condition = ConditionBuilder.byName()
                .match(GoodsQuery::getPrice, Operator.LT)
                .match("(?i).*name.*", Operator.LIKE)
                .filter(f -> {
                    if (f.getField().getName().equals("src_store_name")) {
                        f.setOperator(Operator.NE);
                    }
                })
                .build(query);
        System.out.println(dslContext.renderInlined(condition));
    }


    @Test
    public void testBuilder2() {
        GoodsQuery goodsQuery = new GoodsQuery();
        goodsQuery.setExpireDate(new Date[]{new Date(), new Date()});
        goodsQuery.setSrcStoreName("测试仓");
        goodsQuery.setCreateDate(new Date());
        goodsQuery.setBizType(10);
        Condition condition = ConditionBuilder.byTable(GoodsQueryTable)
                .match(GoodsQuery::getSrcStoreName, Operator.LIKE)
                .build(goodsQuery);
        String sql = dslContext.renderInlined(condition);
        System.out.println(sql);
        assertTrue(sql.contains("src_store_name like"));
    }

}
