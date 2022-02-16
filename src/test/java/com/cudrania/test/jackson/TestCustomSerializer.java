package com.cudrania.test.jackson;

import com.cudrania.core.json.JsonParser;
import com.cudrania.test.bean.Account;
import com.cudrania.test.bean.Account.FullView;
import com.cudrania.test.bean.Account.SimpleView;
import com.cudrania.test.jackson.node.NodeParser;
import com.cudrania.test.jackson.node.RuleNode;
import com.cudrania.test.jackson.serializer.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import lombok.SneakyThrows;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 2022/1/25
 *
 * @author liyifei
 */
public class TestCustomSerializer {


    //通过正则表达式判断是否为敏感字段
    static Sensitive sensitive = new Sensitive("(?i).*(password|balance|phone|id_?card).*");

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
        objectMapper.setSerializerFactory(
                objectMapper.getSerializerFactory().withSerializerModifier(new BeanSerializerModifier() {
                    @Override
                    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                        if (serializer instanceof BeanSerializerBase) {
                            return new TypeAsFieldSerializer(
                                    (BeanSerializerBase) serializer);
                        }
                        return serializer;
                    }
                })
        );
        RuleNode ruleNode = NodeParser.parse("(a||b)&&(!c||!d)", false);
        System.out.println(ruleNode);
        System.out.println(parser.toJson(ruleNode));
    }


    @Test
    public void testSerializerRuleNodeWrapper() {
        JsonParser parser = new JsonParser();
        ObjectMapper objectMapper = parser.getObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        boolean useModule=false;
        if(useModule){
            objectMapper.registerModule(new SimpleModule().addSerializer(new NodeWrapperSerializer()));
        }else{
            objectMapper.setSerializerFactory(
                    objectMapper.getSerializerFactory().withSerializerModifier(new BeanSerializerModifier() {
                        @Override
                        public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                            if (serializer instanceof BeanSerializerBase) {
                                return new RuleNodeSerializer(
                                        (BeanSerializerBase) serializer);
                            }
                            return serializer;
                        }
                    })
            );
        }
        RuleNode ruleNode = NodeParser.parse("(a||b)&&(!c||!d)", true);
        System.out.println(ruleNode);
        System.out.println(parser.toJson(ruleNode));
    }


    @Test
    public void testByModifier() {
        JsonParser parser = new JsonParser();
        ObjectMapper objectMapper = parser.getObjectMapper();
        objectMapper.setSerializerFactory(
                objectMapper.getSerializerFactory().withSerializerModifier(new BeanSerializerModifier() {
                    @Override
                    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                                     BeanDescription beanDesc,
                                                                     List<BeanPropertyWriter> beanProperties) {
                        //修改原有的BeanPropertyWriter列表
                        return beanProperties.stream().map(writer -> new SecurityPropertyWriter(writer, sensitive))
                                .collect(Collectors.toList());
                    }
                })
        );
        Account account = new Account();
        account.setId(1001);
        account.setUserName("jack-wang");
        account.setPassword("pwd12345");
        account.setPhone("18901010001");
        //map无法脱敏
        Map<String, String> map = new HashMap<>();
        map.put("id_card", "110115200810010011");
        map.put("balance", "1314.520");
        map.put("email", "18901010001@qq.com");
        account.setExtras(map);
        System.out.println(parser.toJson(account));
        System.out.println(parser.toJson(map));
    }


    @Test
    @SneakyThrows
    public void testByFilter() {
        JsonParser jsonParser = new JsonParser();
        String filterName = "sec-filter";
        ObjectMapper objectMapper = jsonParser.getObjectMapper();
        objectMapper.setFilterProvider(
                new SimpleFilterProvider().addFilter(filterName, new SecurityPropertyFilter(sensitive))
        );
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public Object findFilterId(Annotated a) {
                return filterName;
            }
        });
        Account account = new Account();
        account.setId(1001);
        account.setUserName("jack-wang");
        account.setPassword("pwd12345");
        account.setPhone("18901010001");
        //map无法脱敏
        Map<String, String> map = new HashMap<>();
        map.put("id_card", "110115200810010011");
        map.put("balance", "1314.520");
        map.put("email", "18901010001@qq.com");
        account.setExtras(map);
        System.out.println(jsonParser.toJson(account));
        System.out.println(jsonParser.toJson(map));
    }


    @Test
    public void testFieldDesc() {
        JsonParser parser = new JsonParser();
        ObjectMapper objectMapper = parser.getObjectMapper();
        objectMapper.setSerializerFactory(
                objectMapper.getSerializerFactory().withSerializerModifier(new BeanSerializerModifier() {
                    @Override
                    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                                     BeanDescription beanDesc,
                                                                     List<BeanPropertyWriter> beanProperties) {
                        //修改原有的BeanPropertyWriter列表
                        return beanProperties.stream().map(writer -> new DescriptionPropertyWriter(writer))
                                .collect(Collectors.toList());
                    }
                })
        );
        Account account = new Account();
        account.setId(1001);
        account.setUserName("jack-wang");
        account.setPassword("pwd12345");
        account.setPhone("18901010001");
        //map无法脱敏
        Map<String, String> map = new HashMap<>();
        map.put("id_card", "110115200810010011");
        map.put("balance", "1314.520");
        map.put("email", "18901010001@qq.com");
        account.setExtras(map);
        System.out.println(parser.toJson(account));
        System.out.println(parser.toJson(map));
    }


    public static void main(String[] args) throws IOException {
        Account account = new Account();
        account.setId(1001);
        account.setUserName("jack-wang");
        account.setPassword("pwd12345");
        account.setPhone("18901010001");
        ObjectMapper objectMapper = new JsonParser().getObjectMapper();
        //序列化，使用视图
        String simple = objectMapper.writerWithView(SimpleView.class).writeValueAsString(account);
        System.out.println(simple);
        String full = objectMapper.writerWithView(FullView.class).writeValueAsString(account);
        System.out.println(full);
        //反序列化，使用视图
        System.out.println((Account)objectMapper.readerWithView(Account.FullView.class).forType(Account.class).readValue(simple));
        System.out.println((Account)objectMapper.readerWithView(Account.FullView.class).forType(Account.class).readValue(full));
    }
}
