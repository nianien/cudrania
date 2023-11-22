package com.cudrania.test.jackson;

import com.cudrania.core.json.JsonParser;
import com.cudrania.test.jackson.node.NodeParser;
import com.cudrania.test.jackson.node.RuleNode;
import com.cudrania.test.jackson.serializer.NodeWrapperSerializer;
import com.cudrania.test.jackson.serializer.RuleNodeSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import org.junit.jupiter.api.Test;

/**
 * Created on 2022/1/25
 *
 * @author liyifei
 */
public class TestNodeSerializer {


    @Test
    public void testByAnnotation() {
        JsonParser parser = new JsonParser();
        ObjectMapper objectMapper = parser.getObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        RuleNode ruleNode = NodeParser.parse("(a||b)&&(!c||!d)", false);
        System.out.println(ruleNode);
        System.out.println(parser.toJson(ruleNode));
    }


    @Test
    public void testSerializerRuleNode() {
        JsonParser parser = new JsonParser();
        ObjectMapper objectMapper = parser.getObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        RuleNode ruleNode = NodeParser.parse("(a||b)&&(!c||!d)", false);
        System.out.println(ruleNode);
        String json = parser.toJson(ruleNode);
        System.out.println(json);
        RuleNode ruleNode1 = parser.toBean(json, RuleNode.class);
        System.out.println(ruleNode1);
        System.out.println(parser.toJson(ruleNode1));
    }


    @Test
    public void testNodeWrapperByModifySerializer() {
        JsonParser parser = new JsonParser();
        parser.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        parser.modifySerializer(serializer -> {
            if (serializer instanceof BeanSerializerBase) {
                return new RuleNodeSerializer(
                        (BeanSerializerBase) serializer);
            }
            return serializer;
        });

        RuleNode ruleNode = NodeParser.parse("(a||b)&&(!c||!d)", true);
        System.out.println(ruleNode);
        System.out.println(parser.toJson(ruleNode));
    }


    @Test
    public void testNodeWrapperByRegisterModule() {
        JsonParser parser = new JsonParser();
        parser.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        parser.registerModule(new SimpleModule().addSerializer(new NodeWrapperSerializer()));
        RuleNode ruleNode = NodeParser.parse("(a||b)&&(!c||!d)", true);
        System.out.println(ruleNode);
        System.out.println(parser.toJson(ruleNode));
    }
}
