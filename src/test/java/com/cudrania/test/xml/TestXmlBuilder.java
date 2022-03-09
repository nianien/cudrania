package com.cudrania.test.xml;

import com.cudrania.core.xml.XMLBuilder;
import com.cudrania.test.bean.Home;
import com.cudrania.test.bean.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestXmlBuilder {

    @Test
    public void test1() {
        Home home = new Home();
        List<User> list = new ArrayList<User>();
        User u1 = new User();
        u1.setId(1001);
        u1.setPassword("password1");
        u1.setUserName("firstMan");
        u1.setUserId("user1001");
        User u2 = new User();
        u2.setId(1002);
        u2.setPassword("password2");
        u2.setUserName("secondMan");
        u2.setUserId("user1002");
        list.add(u1);
        list.add(u2);
        home.setAddress("Beijing,China");
        home.setUsers(list);
        System.out.println(XMLBuilder.xmlWithAttribute("home", home));

        Map<String, Home> homeMap = new HashMap<String, Home>();
        homeMap.put("home1", home);
        homeMap.put("home2", home);
        System.out.println(XMLBuilder.xmlWithAttribute("homes", homeMap));
        List<Home> homeList = new ArrayList<Home>();
        homeList.add(home);
        homeList.add(home);
        System.out.println(XMLBuilder.xmlWithAttribute("homes", homeList));
        System.out.println(XMLBuilder.xmlWithAttribute("homes", "home", homeList));
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", "1001");
        map.put("password", "password1");
        map.put("userName", "firstMan");
        map.put("userId", "user1001");
        mapList.add(map);
        mapList.add(map);
        System.out.println(XMLBuilder.xml("users", "user", mapList));
        System.out.println(XMLBuilder.xmlWithAttribute("users", "user", mapList));
    }

}
