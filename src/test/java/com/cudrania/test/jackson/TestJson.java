package com.cudrania.test.jackson;


import com.cudrania.core.collection.wrapper.Wrappers;
import com.cudrania.core.json.JsonParser;
import com.cudrania.core.math.SmartDecimal;
import com.cudrania.test.bean.Color;
import com.cudrania.test.bean.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.ClassStack;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked", "rawtypes"})
public class TestJson {

    @Test
    public void testPrimitive() {
        JsonParser jp = new JsonParser();
        SmartDecimal smartDecimal = new SmartDecimal(3.1415926);
        System.out.println(jp.toJson(smartDecimal));
        String json = "[1,2]";
        Object obj = jp.toObject(json);
        Assertions.assertEquals(obj.getClass(), ArrayList.class);
        json = "{name:'lining'}";
        obj = jp.toObject(json);
        Assertions.assertEquals(obj.getClass(), LinkedHashMap.class);
        json = "1";
        obj = jp.toObject(json);
        System.out.println(obj.getClass());
        Assertions.assertEquals(obj.getClass(), Integer.class);
        json = "1.0";
        obj = jp.toBean(json, Double.class);
        System.out.println(obj.getClass());
        Assertions.assertEquals(obj.getClass(), Double.class);
        json = "10000000000000000";
        obj = jp.toObject(json);
        Assertions.assertEquals(obj.getClass(), Long.class);
        json = "'1984-10-24'";
        obj = jp.toBean(json, Date.class);
        System.out.println(obj);

        Assertions.assertEquals(obj.getClass(), Date.class);
        jp.withDatePatterns(new String[]{"yyyy年MM月dd日"});
        System.out.println(jp.toJson(new Date()));
        System.out.println(jp.toJson(Color.BLACK));
        obj = jp.toBean("\"BLACK\"", Color.class);
        System.out.println(obj);
        Assertions.assertEquals(obj.getClass(), Color.class);
        obj = jp.toBean("" + Color.BLACK.ordinal(), Color.class);
        System.out.println(obj);

        obj = jp.toObject("'JD.com International Limited'");
        Assertions.assertEquals(obj.getClass(), String.class);
    }


    interface TypeRefer extends Consumer<Map<String, String[]>> {
        void accept(Map<String, List<User>> users);
    }

    @Test
    @SneakyThrows
    public void testParseType() {
        JsonParser jp = new JsonParser();
        Map<String, List<User>> map = new HashMap<>();
        map.put("name",Wrappers.<User>list().$add(new User("18","jack")).$add(new User("19","tom")).get());
        String json = jp.toJson(map);
        Map bean = jp.toBean(json, Map.class);
        List list=(List) bean.get("name");
        System.out.println(list.get(0).getClass());
        Type type = TypeRefer.class.getDeclaredMethods()[0].getGenericParameterTypes()[0];
        Object result = jp.toBean(json, new TypeReference<>() {
            @Override
            public Type getType() {
                return type;
            }
        });
        System.out.println(List.class.cast(((Map)result).get("name")).get(0).getClass());
        result = jp.toBean(json, new TypeReference<Map<String, List<User>>>() {
        });
        System.out.println(List.class.cast(((Map)result).get("name")).get(0).getClass());
    }

    @Test
    public void testParseMap() throws Exception {
        JsonParser jp = new JsonParser();
        String json = "{name:['lining','wuhao']}";
        Map<String, List> map2 = jp.toMap(json, String.class, List.class);
        System.out.println(map2);
        Map<String, String[]> map3 = jp.toMap(json, String.class, String[].class);
        System.out.println(map3);
        TypeFactory typeFactory = jp.getObjectMapper().getTypeFactory();
        TypeBindings typeBindings = TypeBindings.createIfNeeded(
                Map.class,
                new JavaType[]{typeFactory.constructType(String.class),
                        typeFactory.constructType(String[].class)}
        );
        Method method = TypeFactory.class.getDeclaredMethod("_fromClass", ClassStack.class, Class.class, TypeBindings
                .class);
        method.setAccessible(true);
        JavaType result = (JavaType) method.invoke(typeFactory, null, Map.class,
                typeBindings);
        HashMap<String, String[]> map4 = jp.getObjectMapper().readValue(json, result);
        System.out.println(map4);

    }

    @Test
    public void testParseList() {
        JsonParser jp = new JsonParser();
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setId(i);
            user.setUserName("user" + i);
            users.add(user);
        }
        String json = jp.toJson(users);
        System.out.println(json);
        users = jp.toList(json, User.class);
        for (User user : users) {
            System.out.println(user);
        }
        User[] users1 = jp.toArray(json, User.class);
        for (User user : users1) {
            System.out.println(user);
        }
    }

    @Test
    public void testParseArray() {
        JsonParser jp = new JsonParser();
        String[][] arr = new String[2][];
        arr[0] = new String[]{"zg", "\"中国"};
        arr[1] = new String[]{"mg", "美国"};
        System.out.println(jp.toJson(arr));
        String json = jp.toJson(arr);
        arr = jp.toArray(json, String[].class);
        for (String[] s : arr) {
            System.out.println(Arrays.toString(s));
        }
    }

    @Test
    public void testOther() {
        JsonParser jp = new JsonParser();
        String json = "[[\"zg\",\"\\\"中国\"],[\"mg\",\"美国\"]]";
        System.out.println(json);
        String[][] arr = jp.toBean(json, String[][].class);
        for (String[] s : arr) {
            System.out.println(Arrays.toString(s));
        }
        List<List<String>> list = jp.toBean(json, List.class);
        for (List<String> ss : list) {
            System.out.println(ss);
        }
        json = "['lining','wuhao']";
        String[] obj = jp.toBean(json, String[].class);
        System.out.println(Arrays.toString(obj));
        json = "[{name:['lining']},{name:'wuhao'}]";
        Map[] maps = jp.toBean(json, Map[].class);
        for (Map m : maps) {
            System.out.println(m);
        }
        List<Map> objList = (List<Map>) jp.toBean(json, Object.class);
        for (Object l : objList) {
            Map m = (Map) l;
            System.out.println(m);
        }
    }

}
