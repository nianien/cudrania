package com.cudrania.core.json.serializer;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

/**
 * 支持JSON序列化时对POJO提供字段描述<br/>
 * <pre>
 *    objectMapper.setSerializerFactory(
 *
 *      objectMapper.getSerializerFactory()
 *
 *      .withSerializerModifier(new BeanSerializerModifier() {
 *
 *          <code>@Override</code>
 *          public List&lt;BeanPropertyWriter&gt; changeProperties(SerializationConfig config,
 *              BeanDescription beanDesc, List&lt;BeanPropertyWriter&gt; beanProperties) {
 *
 *                  //修改原有的BeanPropertyWriter列表
 *                  return beanProperties
 *                          .stream()
 *                          .map(writer -> new DescriptionPropertyWriter(writer))
 *                          .collect(Collectors.toList());
 *              }
 *      }
 *    )
 * );
 * </pre>
 */
public class DescriptionPropertyWriter extends BeanPropertyWriter {


    private final BeanPropertyWriter writer;

    /**
     * 根据{@link JsonPropertyDescription}添加序列化字段
     *
     * @param writer
     */
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