package com.cudrania.test.jackson.node;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

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
}
