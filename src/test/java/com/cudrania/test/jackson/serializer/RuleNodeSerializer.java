package com.cudrania.test.jackson.serializer;

import com.cudrania.test.jackson.node.NodeWrapper;
import com.cudrania.test.jackson.node.RuleNode;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则节点序列化
 */
public class RuleNodeSerializer extends BeanSerializerBase {

    private Map<Class, String> classAsType = new ConcurrentHashMap<>();

    public RuleNodeSerializer(RuleNodeSerializer src, ObjectIdWriter objectIdWriter, Object propertyFilterId) {
        super(src, objectIdWriter, propertyFilterId);
    }

    public RuleNodeSerializer(RuleNodeSerializer ruleNodeSerializer, Set<String> toIgnore, Set<String> toInclude) {
        super(ruleNodeSerializer, toIgnore, toInclude);
    }


    public RuleNodeSerializer(BeanSerializerBase src) {
        super(src);
    }


    @Override
    protected BeanSerializerBase asArraySerializer() {
        /* Cannot:
         *
         * - have Object Id (may be allowed in future)
         * - have "any getter"
         * - have per-property filters
         */
        if ((_objectIdWriter == null)
                && (_anyGetterWriter == null)
                && (_propertyFilterId == null)
        ) {
            return new BeanAsArraySerializer(this);
        }
        // already is one, so:
        return this;
    }


    @Override
    public BeanSerializerBase withFilterId(Object filterId) {
        return new RuleNodeSerializer(this, _objectIdWriter, filterId);
    }

    @Override
    protected BeanSerializerBase withProperties(BeanPropertyWriter[] beanPropertyWriters, BeanPropertyWriter[] beanPropertyWriters1) {
        return null;
    }

    @Override
    public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter) {
        return new RuleNodeSerializer(this, objectIdWriter, _propertyFilterId);
    }


    @Override
    protected BeanSerializerBase withByNameInclusion(Set<String> toIgnore,
                                                     Set<String> toInclude) {
        return new RuleNodeSerializer(this, toIgnore, toInclude);
    }


    @Override
    public void serialize(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (bean instanceof NodeWrapper) {
            bean = ((NodeWrapper) bean).unwrap();
            provider.findValueSerializer(bean.getClass()).serialize(bean, gen, provider);
            return;
        }
        if (_objectIdWriter != null) {
            gen.setCurrentValue(bean); // [databind#631]
            _serializeWithObjectId(bean, gen, provider, true);
            return;
        }
        gen.writeStartObject(bean);
        if (_propertyFilterId != null) {
            serializeFieldsFiltered(bean, gen, provider);
        } else {
            serializeFields(bean, gen, provider);
        }
        gen.writeEndObject();
    }

    @Override
    protected void serializeFields(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (bean instanceof RuleNode) {
            String type = classAsType.computeIfAbsent(bean.getClass(), clazz -> clazz.getSimpleName().toUpperCase().replaceAll("NODE", ""));
            gen.writeStringField("type", type);
        }
        super.serializeFields(bean, gen, provider);
    }

}