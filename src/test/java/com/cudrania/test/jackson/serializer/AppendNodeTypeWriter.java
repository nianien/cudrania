package com.cudrania.test.jackson.serializer;

import com.cudrania.test.jackson.node.RuleNode;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.fasterxml.jackson.databind.util.Annotations;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AppendNodeTypeWriter extends VirtualBeanPropertyWriter {


    private AppendNodeTypeWriter(BeanPropertyDefinition propDef, Annotations contextAnnotations, JavaType declaredType) {
        super(propDef, contextAnnotations, declaredType);
    }


    @Override
    protected Object value(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
        if (bean instanceof RuleNode) {
            return bean.getClass().getSimpleName().toUpperCase().replaceAll("NODE", "");
        }
        return null;
    }

    @Override
    public VirtualBeanPropertyWriter withConfig(MapperConfig<?> config, AnnotatedClass declaringClass, BeanPropertyDefinition propDef, JavaType type) {
        return new AppendNodeTypeWriter(propDef, declaringClass.getAnnotations(), type);
    }
}