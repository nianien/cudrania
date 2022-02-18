package com.cudrania.test.jackson.node;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.SimpleType;

import java.io.IOException;

/**
 * Created on 2022/2/16
 *
 * @author liyifei
 */
public class MyTypeIdResolver extends TypeIdResolverBase {
    @Override
    public String idFromValue(Object value) {
        return value.getClass().getSimpleName().toUpperCase().replaceAll("NODE", "");
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        System.out.println(suggestedType);
        return idFromValue(value);
    }

    @Override
    public Id getMechanism() {
        return Id.CUSTOM;
    }


    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        switch (id) {
            case "AND":
                return SimpleType.constructUnsafe(AndNode.class);
            case "NOT":
                return SimpleType.constructUnsafe(NotNode.class);
            case "OR":
                return SimpleType.constructUnsafe(OrNode.class);
            case "EXPR":
                return SimpleType.constructUnsafe(ExprNode.class);
        }
        return super.typeFromId(context, id);
    }
}
