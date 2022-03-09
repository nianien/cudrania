package com.cudrania.test.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cudrania.core.reflection.Reflections;
import com.cudrania.test.bean.Home;
import com.cudrania.test.bean.User;

public class TestReflect {

    class A<T> {

        public void dot(T t) {

        }
    }

    class B extends A<User> {

        public void dot(User t) {
        }
    }

    public static List<Method> getAllMethods(Class<?> clazz) {
        List<Method> list = new ArrayList<Method>();
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            for (Method method : clazz.getDeclaredMethods()) {
                list.add(method);
            }
        }
        return list;
    }

    @Test
    public void test() throws Exception {

        List<Method> allMethods = getAllMethods(B.class);
        for (Method allMethod : allMethods) {
            System.out.println(allMethod);
        }

    }

    @Test
    public void testInstance() throws Exception {
        Home h = new Home();
        h.setAddress("aaa");
        h.setUsers(null);
        Home b = new Home();
        Reflections.copyProperties(b, h);
        System.out.println(b.getAddress());

        Class clazz = Integer.class;
        int r = (Integer) clazz.getConstructor(String.class).newInstance("1");
        assert r == 1;
        int n = Reflections.simpleInstance(Integer.TYPE, "1");
        assert n == 1;

    }

}
