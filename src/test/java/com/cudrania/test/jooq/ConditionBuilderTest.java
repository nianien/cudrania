package com.cudrania.test.jooq;

import com.cudrania.side.jooq.ConditionBuilder;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameStyle;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * scm.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
public class ConditionBuilderTest {

    protected DSLContext dslContext;

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
    public void test() {
        GoodsQuery query = new GoodsQuery();
        query.setSubmitTimeBegin(new Date());
        query.setSubmitTimeEnd(new Date());
        System.out.println(new ConditionBuilder().generator(false)
                .filter(f -> f.getField().getDeclaringClass() == GoodsQuery.class).build(query));
    }
}
