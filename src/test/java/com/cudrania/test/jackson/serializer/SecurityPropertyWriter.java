package com.cudrania.test.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

/**
 * 只支持POJO对象脱敏
 */
public class SecurityPropertyWriter extends BeanPropertyWriter {

    private final Sensitive sensitive;

    private final BeanPropertyWriter writer;

    public SecurityPropertyWriter(BeanPropertyWriter writer, Sensitive sensitive) {
        super(writer);
        this.writer = writer;
        this.sensitive = sensitive;
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
        if (sensitive.shouldEncrypt(name, value)) {
            value = sensitive.encrypt(name, value);
        }
        gen.writeObjectField(name, value);
    }


}