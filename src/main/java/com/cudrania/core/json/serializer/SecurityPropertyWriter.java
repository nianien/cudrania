package com.cudrania.core.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * 支持JSON序列化时对POJO对象加密<br/>
 * <pre>
 *  {@link SimpleSerMasker} encryptor = new {@link SimpleSerMasker}("(?i).*(password|balance|phone|id_?card).*");
 *  objectMapper.setSerializerFactory(
 *
 *       objectMapper.getSerializerFactory()
 *       .withSerializerModifier(new {@link  BeanSerializerModifier}() {
 *
 *          <code>@Override</code>
 *          public List&lt;BeanPropertyWriter&gt; changeProperties(SerializationConfig config,
 *                 BeanDescription beanDesc,List&lt;BeanPropertyWriter&gt; beanProperties) {
 *
 *            //修改原有的BeanPropertyWriter列表
 *            return beanProperties
 *                  .stream()
 *                  .map(writer -> new {@link  SecurityPropertyFilter}(writer, encryptor))
 *                  .collect(Collectors.toList());
 *          }
 *      })
 *   );
 * </pre>
 *
 * @author liyifei
 */
public class SecurityPropertyWriter extends BeanPropertyWriter {

    /**
     * 脱敏器
     */
    private final SerMasker masker;

    private final BeanPropertyWriter writer;

    /**
     * @param writer 被代理的字段写入器
     * @param masker 字段脱敏器
     */
    public SecurityPropertyWriter(BeanPropertyWriter writer, SerMasker masker) {
        super(writer);
        this.writer = writer;
        this.masker = masker;
    }


    @Override
    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {

        String name = writer.getName();
        Object value = writer.get(bean);
        if (value == null) {
            return;
        }
        if (masker.shouldMask(name)) {
            value = masker.mask(name, value);
        }
        gen.writeObjectField(name, value);
    }


}