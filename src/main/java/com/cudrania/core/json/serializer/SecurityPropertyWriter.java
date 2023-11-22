package com.cudrania.core.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * 支持JSON序列化时对POJO对象加密<p/>
 * <pre>
 *  {@link RegexSerEncryptor} sensitive = new {@link RegexSerEncryptor}("(?i).*(password|balance|phone|id_?card).*");
 *  objectMapper.setSerializerFactory(
 *
 *       objectMapper.getSerializerFactory()
 *       .withSerializerModifier(new {@link  BeanSerializerModifier}() {
 *
 *          <code>@Override</code>
 *          public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
 *                 BeanDescription beanDesc,List<BeanPropertyWriter> beanProperties) {
 *
 *            //修改原有的BeanPropertyWriter列表
 *            return beanProperties
 *                  .stream()
 *                  .map(writer -> new {@link  SecurityPropertyFilter}(writer, sensitive))
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
     * 加密器
     */
    private final SerEncryptor encryptor;

    private final BeanPropertyWriter writer;

    /**
     * @param writer    被代理的字段写入器
     * @param encryptor 字段加密器
     */
    public SecurityPropertyWriter(BeanPropertyWriter writer, SerEncryptor encryptor) {
        super(writer);
        this.writer = writer;
        this.encryptor = encryptor;
    }


    @Override
    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {

        String name = writer.getName();
        Object value = writer.get(bean);
        if (value == null) {
            return;
        }
        if (encryptor.shouldEncrypt(name, value)) {
            value = encryptor.encrypt(name, value);
        }
        gen.writeObjectField(name, value);
    }


}