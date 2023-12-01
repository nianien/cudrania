package com.cudrania.core.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.std.MapProperty;

import java.util.List;

/**
 * JSON序列化时，对POJO和& Map对象兼容PB格式，对集合字段自动添加xxxList<p/>
 * <pre>
 *  objectMapper.setFilterProvider(
 *
 *    new {@link SimpleFilterProvider }().addFilter(filterName, new {@link  PbPropertyFilter}())
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
public class PbPropertyFilter extends SimpleBeanPropertyFilter {


    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (pojo == null) {
            return;
        }
        String name = writer.getName();
        if (writer instanceof MapProperty) {
            MapProperty mw = (MapProperty) writer;
            Object value = mw.getValue();
            super.serializeAsField(pojo, jgen, provider, writer);
            if (value instanceof List<?> || value.getClass().isArray()) {
                jgen.writeObjectField(name + "List", value);
            }
            return;
        } else if (writer instanceof BeanPropertyWriter) {
            BeanPropertyWriter bw = (BeanPropertyWriter) writer;
            Object value = bw.get(pojo);
            jgen.writeObjectField(name, value);
            if (value instanceof List<?> || value.getClass().isArray()) {
                jgen.writeObjectField(name + "List", value);
            }
            return;
        }
        super.serializeAsField(pojo, jgen, provider, writer);

    }
}
