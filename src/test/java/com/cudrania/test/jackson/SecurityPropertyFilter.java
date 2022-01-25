package com.cudrania.test.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.std.MapProperty;

/**
 * Created on 2022/1/25
 *
 * @author liyifei
 */
public class SecurityPropertyFilter extends SimpleBeanPropertyFilter {


    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (pojo == null) {
            return;
        }
        String name = writer.getName();
        Object value;
        if (name.equalsIgnoreCase("password")) {
            if (writer instanceof MapProperty) {
                MapProperty mw = (MapProperty) writer;
                value = mw.getValue();
                if (value == null) {
                    return;
                }
                if (value instanceof String) {
                    mw.setValue("*****");
                    super.serializeAsField(pojo, jgen, provider, writer);
                    return;
                }
            } else if (writer instanceof BeanPropertyWriter) {
                BeanPropertyWriter bw = (BeanPropertyWriter) writer;
                value = bw.get(pojo);
                if (value == null) {
                    return;
                }
                if (value instanceof String) {
                    jgen.writeObjectField(name, "****");
                    return;
                }
            }
        } else {
            super.serializeAsField(pojo, jgen, provider, writer);
        }

    }

    @Override
    public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (elementValue == null) {
            return;
        }
        if (writer instanceof MapProperty) {
            if (writer.getName().equalsIgnoreCase("password")) {
                ((MapProperty) writer).setValue("*****");
            }
        }
        super.serializeAsElement(elementValue, jgen, provider, writer);
    }
}
