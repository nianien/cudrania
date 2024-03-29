package com.cudrania.test.jackson;

import com.cudrania.core.json.JsonParser;
import com.cudrania.core.json.serializer.*;
import com.cudrania.test.bean.Account;
import com.cudrania.test.bean.Account.FullView;
import com.cudrania.test.bean.Account.SimpleView;
import com.cudrania.test.bean.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2022/1/25
 *
 * @author liyifei
 */
public class TestCustomSerializer {


    //通过正则表达式判断是否为敏感字段

    static SerEncryptor encryptor = new SimpleSerEncryptor("(?i).*(password|balance|phone|id_?card).*", "****");

    /**
     * 序列化字段加密
     */
    @Test
    public void testBySerEncryptor() {
        JsonParser parser = new JsonParser().withSerEncryptor(encryptor);
        Account account = new Account();
        account.setId(1001);
        account.setUserName("jack-wang");
        account.setPassword("http://www.baidu.com");
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


    /**
     * 序列化字段加密
     */
    @Test
    public void testByModifier() {
        JsonParser parser = new JsonParser()
                .modifyPropertyWriter(w -> new SecurityPropertyWriter(w, encryptor));
        Account account = new Account();
        account.setId(1001);
        account.setUserName("jack-wang");
        account.setPassword("http://www.baidu.com");
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

    /**
     * 序列化使用过滤器加密
     */
    @Test
    @SneakyThrows
    public void testByFilter() {
        JsonParser jsonParser = new JsonParser()
                .withPropertyFilter(
                        new SecurityPropertyFilter(encryptor));
        Account account = new Account();
        account.setId(1001);
        account.setUserName("jack-wang");
        account.setPassword("pwd12345");
        account.setPhone("18901010001");
        //map脱敏
        Map<String, String> map = new HashMap<>();
        map.put("id_card", "110115200810010011");
        map.put("balance", "1314.520");
        map.put("email", "18901010001@qq.com");

        User user = new User();
        user.setPassword("1233");
        account.setExtras(map);
        System.out.println(jsonParser.toJson(account));
        System.out.println(jsonParser.toJson(map));
    }


    /**
     * 序列化自动添加字段描述
     */
    @Test
    public void testPbFieldFromBean() {
        JsonParser jp = new JsonParser()
                .modifyPropertyWriter(writer -> new PbPropertyWriter(writer));
        String json = "{\"id\":1001,\"userName\":\"jack-wang\",\"favorites\":[\"apple\",\"banana\",\"orange\"],\"password\":\"pwd12345\",\"phone\":\"18901010001\",\"extras\":{\"balance\":\"1314.520\",\"id_card\":\"110115200810010011\",\"email\":\"18901010001@qq.com\"}}";
        System.out.println(jp.toJson(jp.toBean(json, Account.class)));
    }


    /**
     * 序列化自动添加字段描述
     */
    @Test
    public void testParsePbFromJson() {
        JsonParser jp = new JsonParser()
                .withPropertyFilter(
                        new PbPropertyFilter());
        String json = "{\"id\":1001,\"userName\":\"jack-wang\",\"favorites\":[\"apple\",\"banana\",\"orange\"],\"password\":\"pwd12345\",\"phone\":\"18901010001\",\"extras\":{\"balance\":\"1314.520\",\"id_card\":\"110115200810010011\",\"email\":\"18901010001@qq.com\"}}";
        System.out.println(jp.toJson(jp.toObject(json)));
    }

    /**
     * 序列化自动添加字段描述
     */
    @Test
    public void testFieldDesc() {
        JsonParser parser = new JsonParser()
                .modifyPropertyWriter(writer -> new DescriptionPropertyWriter(writer));

        Account account = new Account();
        account.setId(1001);
        account.setFavorites(Arrays.asList("apple", "banana", "orange"));
        account.setUserName("jack-wang");
        account.setPassword("pwd12345");
        account.setPhone("18901010001");
        Map<String, String> map = new HashMap<>();
        map.put("id_card", "110115200810010011");
        map.put("balance", "1314.520");
        map.put("email", "18901010001@qq.com");
        account.setExtras(map);
        System.out.println(parser.toJson(account));
        System.out.println(parser.toJson(map));
    }


    /**
     * 序列化使用视图
     *
     * @throws IOException
     */
    @Test
    public void testView() throws IOException {
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
        System.out.println((Account) objectMapper.readerWithView(Account.FullView.class).forType(Account.class).readValue(simple));
        System.out.println((Account) objectMapper.readerWithView(Account.FullView.class).forType(Account.class).readValue(full));
    }
}
