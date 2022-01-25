package com.cudrania.test.jackson;

import com.cudrania.core.json.JsonParser;
import com.cudrania.test.bean.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2022/1/25
 *
 * @author liyifei
 */
public class TestCustomSerializer {


    @Test
    public void testByModifier() {
        JsonParser parser = new JsonParser();
        ObjectMapper objectMapper = parser.getObjectMapper();
        objectMapper.setSerializerFactory(
                objectMapper.getSerializerFactory().withSerializerModifier(new CustomBeanSerializerModifier())
        );
        User user = new User();
        user.setId(1001);
        user.setUserName("jack-ma");
        user.setPassword("12345");
        user.setUserDesc(new String[]{"desc1:aaa", "desc1:bbbb"});
        Map<String, String> map = new HashMap<>();
        map.put("password", "1234");
        map.put("phone", "1890101");
        user.setExtras(map);
        System.out.println(parser.toJson(user));
        System.out.println(parser.toJson(map));
    }


    @Test
    public void testByFilter() {
        final String filterName = "sec-filter";
        JsonParser parser = new JsonParser();
        ObjectMapper objectMapper = parser.getObjectMapper();
        objectMapper.setFilterProvider(
                new SimpleFilterProvider().addFilter("sec-filter", new SecurityPropertyFilter())
        );
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public Object findFilterId(Annotated a) {
                return filterName;
            }
        });
        User user = new User();
        user.setId(1001);
        user.setUserName("jack-ma");
        user.setPassword("12345");
        user.setUserDesc(new String[]{"desc1:aaa", "desc1:bbbb"});
        Map<String, String> map = new HashMap<>();
        map.put("password", "1234");
        map.put("phone", "1890101");
        user.setExtras(map);
        System.out.println(parser.toJson(user));
        System.out.println(parser.toJson(map));
    }

}
