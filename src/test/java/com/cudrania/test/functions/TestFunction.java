package com.cudrania.test.functions;

import com.cudrania.core.functions.Fluent;
import com.cudrania.core.functions.Params;
import com.cudrania.core.functions.Params.ImmutableParam;
import com.cudrania.core.utils.StringUtils;
import com.cudrania.test.bean.Contact;
import com.cudrania.test.bean.Family;
import com.cudrania.test.bean.People;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.cudrania.core.functions.Fluent.of;
import static com.cudrania.core.functions.Params.gt0;

/**
 * @author scorpio
 * @version 1.0.0
 */
public class TestFunction {


    @Test
    public void testParam() {

        ImmutableParam<String> param = Params.with(10).then(n -> {
            System.out.println("====");
            return n * n;
        }).when(n -> {
            System.out.println("====");
            return n < 200;
        }).then(n -> {
            System.out.println("====");
            return "(" + n + ")";
        });
        System.out.println(">>>>>>>>>>>>>");
        System.out.println(param.get());
    }

    @Test
    public void testAll() {

        System.out.println(
                of(new Family()).$$(Family::getHost).$$(People::getContact).$$(Contact::getAddress).get()
        );
        System.out.println(
                of((Family) null).$$(Family::getHost).$$(People::getContact).$$(Contact::getAddress).get()
        );

        Family family = new Family();


        of(family).$$(Family::getHost).$$(People::getContact).$$(Contact::getAddress).get();


        People people = of(new People())
                .$(People::setId, 101L)
                .method(People::setName)
                .invoke("name")
                .$(People::setName, "name")
                .$(People::setName, "name")
                .$(People::setSex, "male")
                .$(People::setBirthday, new Date())
                .$(People::setContact,
                        of(new Contact())
                                .$(Contact::setAddress, "address*****")
                                .$(Contact::setTelephone, "137****")
                                .$(Contact::setEmail, "email****")
                                .get()
                ).get();


        Fluent.of(people)
                .$(gt0(people.getId()).then(id -> id.longValue()), (p, id) -> p.setId(id))
                .$$(gt0(people.getId()).then(id -> id.longValue()), (p, id) -> {
                    p.setId(id);
                    return p;
                });

        family.setHost(people);
        System.out.println(of(family)
                .$(Family::setAddress, "a")
                .$(f -> f.setAddress("b"))
                .$(System.out::println)
                .get());

        Map<String, String> map1 = of(new HashMap<String, String>())
                .$(Map::put, "a", "Alex")
                .$(Map::put, "b", "Brown")
                .$(Map::put, "c", "Charles")
                .$(Map::put, "d", "Darwin")
                .get();
        System.out.println(map1);
        Map<String, String> map2 = of(new HashMap<String, String>())
                .<String, String>method(Map::put)
                .invoke("a", "Alex")
                .invoke("b", "Brown")
                .invoke("c", "Charles")
                .invoke("d", "Darwin")
                .<String>method(Map::remove)
                .invoke("b")
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
                .<String>method(StringBuilder::append)
                .invoke(Params.with(id).when(e -> e > 0).then(e -> e.toString()))
                .invoke(Params.notEmpty(name))
                .invoke(Params.notEmpty(email))
                .get().toString();
    }


}
