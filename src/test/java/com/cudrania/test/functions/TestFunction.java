package com.cudrania.test.functions;

import com.cudrania.core.functions.Fluent;
import com.cudrania.core.functions.Params;
import com.cudrania.core.utils.StringUtils;
import com.cudrania.test.bean.Contact;
import com.cudrania.test.bean.Family;
import com.cudrania.test.bean.People;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.cudrania.core.functions.Fluent.of;
import static com.cudrania.core.functions.Params.gt0;

/**
 * @author scorpio
 * @version 1.0.0
 * @email tengzhe.ln@alibaba-inc.com
 */
public class TestFunction {


    public static void main(String[] args) {


        System.out.println(
                of(new Family()).apply(Family::getHost).apply(People::getContact).apply(Contact::getAddress).get()
        );
        System.out.println(
                of((Family) null).apply(Family::getHost).apply(People::getContact).apply(Contact::getAddress).get()
        );

        Family family = new Family();


        of(family).apply(Family::getHost).apply(People::getContact).apply(Contact::getAddress).get();


        People people = of(new People())
                .accept(People::setId, 101L)
                .bind(People::setName)
                .call("name")
                .accept(People::setName, "name")
                .accept(People::setName, "name")
                .accept(People::setSex, "male")
                .accept(People::setBirthday, new Date())
                .accept(People::setContact,
                        of(new Contact())
                                .accept(Contact::setAddress, "address*****")
                                .accept(Contact::setTelephone, "137****")
                                .accept(Contact::setEmail, "email****")
                                .get()
                ).get();


        Fluent.of(people)
                .accept(gt0(people.getId()).then(id -> id.longValue()), (p, id) -> p.setId(id))
                .apply(gt0(people.getId()).then(id -> id.longValue()), (p, id) -> {
                    p.setId(id);
                    return p;
                });

        family.setHost(people);
        System.out.println(of(family)
                .accept(Family::setAddress, "a")
                .accept(f -> f.setAddress("b"))
                .accept(System.out::println)
                .get());

        Map<String, String> map1 = of(new HashMap<String, String>())
                .accept(Map::put, "a", "Alex")
                .accept(Map::put, "b", "Brown")
                .accept(Map::put, "c", "Charles")
                .accept(Map::put, "d", "Darwin")
                .get();
        System.out.println(map1);
        Map<String, String> map2 = of(new HashMap<String, String>())
                .<String, String>bind(Map::put)
                .call("a", "Alex")
                .call("b", "Brown")
                .call("c", "Charles")
                .call("d", "Darwin")
                .<String>bind(Map::remove)
                .call("b")
                .get();

        System.out.println(map2);

        System.out.println(build1(1L, "test", ""));
        System.out.println(build2(1L, "test", ""));
    }


    static String build1(long id, String name, String email) {
        StringBuilder builder = new StringBuilder();
        if (id > 0) {
            builder.append(id);
        }
        if (StringUtils.isNotEmpty(name)) {
            builder.append(name);
        }
        if (StringUtils.isNotEmpty(email)) {
            builder.append(email);
        }
        return builder.toString();
    }

    static String build2(long id, String name, String email) {
        return of(new StringBuilder())
                .<String>bind(StringBuilder::append)
                .call(Params.with(id).when(e -> e > 0).then(e -> e.toString()))
                .call(Params.notEmpty(name))
                .call(Params.notEmpty(email))
                .get().toString();
    }


}
