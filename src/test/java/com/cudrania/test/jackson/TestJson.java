package com.cudrania.test.jackson;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.ClassStack;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.cudrania.core.json.JsonParser;
import com.cudrania.test.bean.Color;
import com.cudrania.test.bean.User;
import org.junit.jupiter.api.Test;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings({"unchecked", "rawtypes"})
public class TestJson {

    @Test
    public void testPrimitive() {
        JsonParser jp = new JsonParser();
        FinanceDecimal financeDecimal = new FinanceDecimal("3.1415926");
        System.out.println(jp.toJson(financeDecimal));
    }

    @Test
    public void testBase() {
        JsonParser jp = new JsonParser();
        String json = "[1,2]";
        Object obj = jp.toObject(json);
        System.out.println(obj.getClass());
        json = "{name:'lining'}";
        obj = jp.toObject(json);
        System.out.println(obj.getClass());
        json = "1";
        obj = jp.toObject(json);
        System.out.println(obj.getClass());
        json = "1.0";
        obj = jp.toObject(json);
        System.out.println(obj.getClass());
        json = "10000000000000000";
        obj = jp.toObject(json);
        System.out.println(obj.getClass());
        json = "'1984-10-24'";
        obj = jp.toBean(json, Date.class);
        System.out.println(obj);
        System.out.println(obj.getClass());
        jp.setDatePatterns(new String[]{"yyyy年MM月dd日"});
        System.out.println(jp.toJson(new Date()));

        System.out.println(jp.toJson(Color.BLACK));
        obj = jp.toBean("\"BLACK\"", Color.class);
        System.out.println(obj);
        obj = jp.toBean("" + Color.BLACK.ordinal(), Color.class);
        System.out.println(obj);
    }


    @Test
    public void testMap() throws Exception {
        JsonParser jp = new JsonParser();
        String json = "{name:['lining','wuhao']}";
        System.out.println("=============================>1:");
        Map<String, String[]> map = jp.toBean(json, new TypeReference<Map<String, String[]>>() {
        });

        for (Entry<String, String[]> en : map.entrySet()) {
            System.out.print(en.getKey() + ":[");
            String[] values = en.getValue();
            for (String e : values) {
                System.out.print(e + "\t");
            }
            System.out.println("]");
        }
        System.out.println("=============================>2:");

        Map<String, List> map2 = jp.toMap(json, String.class, List.class);
        for (Entry<String, List> en : map2.entrySet()) {
            System.out.print(en.getKey() + ":[");
            List<String> values = en.getValue();
            for (String e : values) {
                System.out.print(e + "\t");
            }
            System.out.println("]");
        }
        System.out.println("=============================>3:");
        Map<String, String[]> map3 = jp.toMap(json, String.class, String[].class);
        for (Entry<String, String[]> en : map3.entrySet()) {
            System.out.print(en.getKey() + ":[");
            String[] values = en.getValue();
            for (String e : values) {
                System.out.print(e + "\t");
            }
            System.out.println("]");
        }

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

        System.out.println("=============================>3:");
        HashMap<String, String[]> map4 = jp.getObjectMapper().readValue(json, result);
        for (Entry<String, String[]> en : map4.entrySet()) {
            System.out.print(en.getKey() + ":[");
            String[] values = en.getValue();
            for (String e : values) {
                System.out.print(e + "\t");
            }
            System.out.println("]");
        }

    }

    @Test
    public void testList() {
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
    public void testArray() {
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
        String[][] arr = new String[2][];
        arr[0] = new String[]{"zg", "\"中国"};
        arr[1] = new String[]{"mg", "美国"};
        System.out.println(jp.toJson(arr));
        String json = jp.toJson(arr);
        arr = jp.toBean(json, String[][].class);
        for (String[] s : arr) {
            System.out.println(Arrays.toString(s));
        }
        System.out.println("=============");
        List<List<String>> list = jp.toBean(json, List.class);
        for (List<String> ss : list) {
            for (String s : ss) {
                System.out.print(s + "\t");
            }
            System.out.println();
        }
        json = "['lining','wuhao']";
        Object obj = jp.toBean(json, String[].class);
        System.out.println(obj.getClass());

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
        json = "[{name:['lining']},{name:'wuhao'}]";
        Map[] omap = jp.toBean(json, Map[].class);
        for (Map m : omap) {
            for (Object o : m.entrySet()) {
                Entry en = (Entry) o;
                System.out.println(en.getKey() + "->" + en.getValue().getClass());
            }
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
        Object objList = jp.toBean(json, Object.class);
        for (Object l : (List) objList) {
            Map m = (Map) l;
            for (Object o : m.entrySet()) {
                Entry en = (Entry) o;
                System.out.println(en.getKey() + "->" + en.getValue().getClass());
            }
        }
    }

}
