package com.cudrania.test.jackson.serializer;

import com.cudrania.test.jackson.node.NodeWrapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Created on 2022/2/15
 *
 * @author liyifei
 */
public class NodeWrapperSerializer extends StdSerializer<NodeWrapper> {
    public NodeWrapperSerializer() {
        super(NodeWrapper.class);
    }

    @Override
    public void serialize(NodeWrapper value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeObject(value.unwrap());
    }

    @Override
    public void serializeWithType(NodeWrapper value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        gen.writeObject(value.unwrap());
    }
}
