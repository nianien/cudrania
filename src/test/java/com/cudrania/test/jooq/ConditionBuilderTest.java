package com.cudrania.test.jooq;

import com.cudrania.side.jooq.ConditionBuilder;
import com.cudrania.side.jooq.FluentCondition;
import com.cudrania.side.jooq.Operator;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameStyle;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.cudrania.core.functions.Params.gt;
import static com.cudrania.core.functions.Params.notEmpty;

/**
 * scm.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
public class ConditionBuilderTest {

    static class UserTable {
        private static final Field<Long> ID = DSL.field("id", Long.class);
        private static final Field<String> NAME = DSL.field("name", String.class);
        private static final Field<Integer> TYPE = DSL.field("type", Integer.class);
    }

    private DSLContext dslContext;

    @Before
    public void setup() {
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
                .withRegex("(?i).*name.*", Operator.LIKE)
                .withName("price", Operator.LT)
                .with(name -> name.equals("industryId"), Operator.NONE)
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
        Condition condition = ConditionBuilder.byUnderLine()
                .withName("SrcStoreName", Operator.LIKE)
                .with((k, v) -> v instanceof Number && ((Number) v).intValue() < 0, Operator.NONE)
                .build(goodsQuery);
        System.out.println(dslContext.renderInlined(condition));
    }

}
