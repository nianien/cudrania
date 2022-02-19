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
        class User {
            private Field<Long> ID = DSL.field("id", Long.class);
            private Field<String> NAME = DSL.field("name", String.class);
            private Field<Integer> TYPE = DSL.field("type", Integer.class);
        }
        User user = new User();
        long id = 100;
        String name = "jack";
        List<Integer> types = Arrays.asList(1001, 1002, 1003, 1004);
        Condition condition = FluentCondition.and()
                .when(gt(id, 10), user.ID)
                .when(notEmpty(name), user.NAME, (f, p) -> f.ne(p))
                .when(notEmpty(types), user.TYPE::notIn)
                .get();

        System.out.println(dslContext.renderInlined(condition));
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
                .with(name->name.equals("industryId"),null)
                .with("price", Operator.LT)
                .build(query);

        System.out.println(dslContext.renderInlined(condition));
    }


    @Test
    public void testBuilder2() {
        AdCert adCertPhoto = new AdCert();
        adCertPhoto.setExpireDate(new Date[]{new Date(), new Date()});
        adCertPhoto.setStatusDetail("测试");
        adCertPhoto.setCreateDate(new Date());
        adCertPhoto.setStatus(-1);
        Condition condition = ConditionBuilder.byUnderLine()
                .with("StatusDetail", Operator.LIKE)
                .with(f -> {
                    if (f.getValue() instanceof Integer) {
                        return ((Integer) f.getValue()) > 0;
                    }
                    return true;
                })
                .with(SQLDialect.MYSQL)
                .build(adCertPhoto);
        System.out.println(dslContext.renderInlined(condition));
    }


}
