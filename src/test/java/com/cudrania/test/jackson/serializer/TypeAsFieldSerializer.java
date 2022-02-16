package com.cudrania.test.jackson.serializer;

import com.cudrania.test.jackson.node.RuleNode;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

import java.io.IOException;

public class TypeAsFieldSerializer extends BeanSerializer {


    public TypeAsFieldSerializer(BeanSerializerBase src) {
        super(src);
    }

    protected TypeAsFieldSerializer() {
        super(null);
    }

    @Override
    protected void serializeFields(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (bean instanceof RuleNode) {
            gen.writeStringField("type", bean.getClass().getSimpleName().toUpperCase().replaceAll("NODE", ""));
        }
        super.serializeFields(bean, gen, provider);

    }
}