package com.cudrania.core.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

/**
 * 只支持POJO对象脱敏
 * <pre>
 *  Sensitive sensitive = new Sensitive("(?i).*(password|balance|phone|id_?card).*");
 *  objectMapper.setSerializerFactory(
 *
 *       objectMapper.getSerializerFactory()
 *       .withSerializerModifier(new BeanSerializerModifier() {
 *
 *       <code>@Override</code>
 *       public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
 *                 BeanDescription beanDesc,List<BeanPropertyWriter> beanProperties) {
 *
 *            //修改原有的BeanPropertyWriter列表
 *            return beanProperties
 *            .stream()
 *            .map(writer -> new SecurityPropertyWriter(writer, sensitive))
 *             .collect(Collectors.toList());
 *            }
 *      })
 *   );
 * </pre>
 *
 * @author liyifei
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