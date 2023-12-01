package com.cudrania.core.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

import java.util.List;

/**
 * 支持JSON序列化时对PB字段兼容,序列化自动添加xxxList<p/>
 * <pre>
 *    objectMapper.setSerializerFactory(
 *
 *      objectMapper.getSerializerFactory()
 *
 *      .withSerializerModifier(new BeanSerializerModifier() {
 *
 *          <code>@Override</code>
 *          public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
 *              BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
 *
 *                  //修改原有的BeanPropertyWriter列表
 *                  return beanProperties
 *                          .stream()
 *                          .map(writer -> new ProtobufPropertyWriter(writer))
 *                          .collect(Collectors.toList());
 *              }
 *      }
 *    )
 * );
 * </pre>
 */
public class ProtobufPropertyWriter extends BeanPropertyWriter {


    private final BeanPropertyWriter writer;

    /**
     * 根据数组或集合类型, 自动添加序列化字段:xxxList
     *
     * @param writer
     */
    public ProtobufPropertyWriter(BeanPropertyWriter writer) {
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
        if (value instanceof List<?> || value.getClass().isArray()) {
            gen.writeObjectField(name + "List", value);
        }

    }

}