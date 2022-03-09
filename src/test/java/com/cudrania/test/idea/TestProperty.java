package com.cudrania.test.idea;

import com.cudrania.idea.properties.Properties;

import org.junit.jupiter.api.Test;

public class TestProperty {

    @Test
    public void test() {

        Properties pp = new Properties("properties.xml");
        String sql = pp.getProperty("delete");
        System.out.println(sql);
        sql = pp.defaultPackage("users").getProperty("update");
        System.out.println(sql);
    }
}
