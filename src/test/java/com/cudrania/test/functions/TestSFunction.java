package com.cudrania.test.functions;

import com.cudrania.core.functions.Fn;
import com.cudrania.core.reflection.Reflections;
import com.cudrania.test.bean.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.Serializable;


/**
 * Created on 2022/3/10
 *
 * @author liyifei
 */
public class TestSFunction implements Serializable {


    @Test
    @SneakyThrows
    public void test() {
        User user = new User();
        System.out.println(Fn.of(TestSFunction::ff).name());
        System.out.println(Fn.of(User::getId).name());
        System.out.println(Fn.of(User::getUserId).name());
        Fn.of(User::setUserId).invoke(user, "111");
        System.out.println(Fn.of(User::getUserId).invoke(user));
        Reflections.invoke(Fn.of(User::setUserName), user, "aaaa");
        System.out.println(user);
    }


    public void ff(int a, int b, String c) {

    }

    public String getName(java.util.function.BiFunction<User, String, User> function) {
        return function.toString();
    }

    public String getName(java.util.function.Function<String, User> function) {

        return function.toString();
    }

}
