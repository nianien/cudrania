package com.cudrania.test.functions;

import com.cudrania.core.functions.Fn;
import com.cudrania.core.functions.Fn.Consumer;
import com.cudrania.test.bean.User;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


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

        Map map = new HashMap();
        Fn.of(map::put);
        Fn.of(Map<String, String>::put);
        Fn.of((Consumer<Type>) System.out::println).accept(new TypeReference<Consumer<Integer>>() {
        }.getType().getClass());
        System.out.println(Fn.of(User::getId).name());
        System.out.println(Fn.of(User::getUserId).name());
        Fn.of(User::setUserId).accept(user, "111");
        Fn.of(User::setUserId).bind(user).bind("222").run();
        System.out.println(Fn.of(User::getUserId).bind(user).call());
        System.out.println(user);
    }


}
