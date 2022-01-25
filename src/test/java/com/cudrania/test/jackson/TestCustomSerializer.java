package com.cudrania.test.jackson;

import com.cudrania.core.json.JsonParser;
import com.cudrania.test.bean.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

/**
 * Created on 2022/1/25
 *
 * @author liyifei
 */
public class TestCustomSerializer {


    @Test
    public void test() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory()
                .withSerializerModifier(new CustomBeanSerializerModifier()));
        JsonParser parser = new JsonParser(objectMapper);
        User user = new User();
        user.setId(1001);
        user.setUserName("jack-ma");
        user.setPassword("12345");
        user.setUserDesc(new String[]{"desc1:aaa", "desc1:bbbb"});
        System.out.println(parser.toJson(user));
    }
}
