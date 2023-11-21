package com.cudrania.core.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.std.MapProperty;

/**
 * 支持POJO & Map脱敏<p/>
 * <pre>
 *  Sensitive sensitive = new Sensitive("(?i).*(password|balance|phone|id_?card).*");
 *
 *  objectMapper.setFilterProvider(
 *
 *    new SimpleFilterProvider().addFilter(filterName, new SecurityPropertyFilter(sensitive))
 *    );
 *
 *   objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
 *       <code>@Override</code>
 *        public Object findFilterId(Annotated a) {
 *           return filterName;
 *       }
 *   });
 * </pre>
 *
 * @author liyifei
 */
public class SecurityPropertyFilter extends SimpleBeanPropertyFilter {

    private Sensitive sensitive;

    /**
     * @param sensitive
     */
    public SecurityPropertyFilter(Sensitive sensitive) {
        this.sensitive = sensitive;
    }


    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (pojo == null) {
            return;
        }
        String name = writer.getName();
        if (writer instanceof MapProperty) {
            MapProperty mw = (MapProperty) writer;
            Object value = mw.getValue();
            if (sensitive.shouldEncrypt(name, value)) {
                mw.setValue(sensitive.encrypt(name, value));
            }
            super.serializeAsField(pojo, jgen, provider, writer);
            return;
        } else if (writer instanceof BeanPropertyWriter) {
            BeanPropertyWriter bw = (BeanPropertyWriter) writer;
            Object value = bw.get(pojo);
            if (sensitive.shouldEncrypt(name, value)) {
                //注意: 这里没有直接调用父类方法,是为了不修改pojo对象
                value = sensitive.encrypt(name, value);
                jgen.writeObjectField(name, value);
                return;
            }
        }
        super.serializeAsField(pojo, jgen, provider, writer);

    }

    @Override
    public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (writer instanceof MapProperty) {
            if (sensitive.shouldEncrypt(writer.getName(), elementValue)) {
                ((MapProperty) writer).setValue(sensitive.encrypt(writer.getName(), elementValue));
            }
        }
        super.serializeAsElement(elementValue, jgen, provider, writer);
    }
}
