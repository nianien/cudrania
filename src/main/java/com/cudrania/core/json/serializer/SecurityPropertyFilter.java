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
 * 支持JSON序列化时对POJO和& Map对象加密<p/>
 * <pre>
 *  {@link SimpleSerEncryptor} encryptor = new {@link SimpleSerEncryptor}("(?i).*(password|balance|phone|id_?card).*");
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
     * 加密器
     */
    private SerEncryptor encryptor;

    /**
     * @param encryptor
     */
    public SecurityPropertyFilter(SerEncryptor encryptor) {
        this.encryptor = encryptor;
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
            if (encryptor.shouldEncrypt(name)) {
                mw.setValue(encryptor.encrypt(name, value));
            }
            super.serializeAsField(pojo, jgen, provider, writer);
            return;
        } else if (writer instanceof BeanPropertyWriter) {
            BeanPropertyWriter bw = (BeanPropertyWriter) writer;
            Object value = bw.get(pojo);
            if (encryptor.shouldEncrypt(name)) {
                //注意: 这里没有直接调用父类方法,是为了不修改pojo对象
                value = encryptor.encrypt(name, value);
                jgen.writeObjectField(name, value);
                return;
            }
        }
        super.serializeAsField(pojo, jgen, provider, writer);

    }

    @Override
    public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (writer instanceof MapProperty) {
            if (encryptor.shouldEncrypt(writer.getName())) {
                ((MapProperty) writer).setValue(encryptor.encrypt(writer.getName(), elementValue));
            }
        }
        super.serializeAsElement(elementValue, jgen, provider, writer);
    }
}
