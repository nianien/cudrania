package com.cudrania.test.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public class CustomBeanPropertyWriter extends BeanPropertyWriter {
    private final BeanPropertyWriter writer;

    public CustomBeanPropertyWriter(BeanPropertyWriter writer) {
        super(writer);
        this.writer = writer;
    }

    @Override
    public void serializeAsField(Object bean,
                                 JsonGenerator gen,
                                 SerializerProvider prov) throws Exception {

        String name = writer.getName();
        Object value = writer.get(bean);
        if (name.equalsIgnoreCase("password") || name.equalsIgnoreCase("pwd")) {
            value = "*****";
        }
        gen.writeObjectField(name, value);
    }

}