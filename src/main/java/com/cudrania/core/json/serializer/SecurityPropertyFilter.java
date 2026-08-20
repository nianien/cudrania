package com.cudrania.core.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.std.MapProperty;

/**
 * 支持JSON序列化时对POJO和&amp; Map对象加密<br/>
 * <pre>
 *  {@link SimpleSerMasker} encryptor = new {@link SimpleSerMasker}("(?i).*(password|balance|phone|id_?card).*");
 *
 *  objectMapper.setFilterProvider(
 *
 *    new {@link SimpleFilterProvider }().addFilter(filterName, new {@link  SecurityPropertyFilter}(encryptor))
 *    );
 *
 *   objectMapper.setAnnotationIntrospector(new {@link JacksonAnnotationIntrospector}() {
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

    /**
     * 脱敏器
     */
    private SerMasker masker;

    /**
     * @param masker 字段脱敏器
     */
    public SecurityPropertyFilter(SerMasker masker) {
        this.masker = masker;
    }


    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (pojo == null) {
            return;
        }
        String name = writer.getName();
        if (writer instanceof BeanPropertyWriter) {
            BeanPropertyWriter bw = (BeanPropertyWriter) writer;
            Object value = bw.get(pojo);
            if (masker.shouldMask(name)) {
                //这里由于pojo对象未发生改变, 所以只能手动直接序列化
                value = masker.mask(name, value);
                jgen.writeObjectField(name, value);
                return;
            }
        } else if (writer instanceof MapProperty) {
            MapProperty mw = (MapProperty) writer;
            Object value = mw.getValue();
            if (masker.shouldMask(name)) {
                mw.setValue(masker.mask(name, value));
            }
        }
        super.serializeAsField(pojo, jgen, provider, writer);

    }

    @Override
    public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (writer instanceof MapProperty) {
            if (masker.shouldMask(writer.getName())) {
                ((MapProperty) writer).setValue(masker.mask(writer.getName(), elementValue));
            }
        }
        super.serializeAsElement(elementValue, jgen, provider, writer);
    }
}
