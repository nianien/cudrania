package com.cudrania.test.jackson.serializer;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

/**
 * 提供字段描述
 */
public class DescriptionPropertyWriter extends BeanPropertyWriter {


    private final BeanPropertyWriter writer;

    public DescriptionPropertyWriter(BeanPropertyWriter writer) {
        super(writer);
        this.writer = writer;
    }

    @Override
    public void serializeAsField(Object bean,
                                 JsonGenerator gen,
                                 SerializerProvider prov) throws Exception {

        String name = writer.getName();
        Object value = writer.get(bean);
        if (value == null) {
            return;
        }
        super.serializeAsField(bean, gen, prov);
        JsonPropertyDescription annotation = writer.getAnnotation(JsonPropertyDescription.class);
        if (annotation != null) {
            gen.writeObjectField(name + "_desc", annotation.value());
        }
    }

}