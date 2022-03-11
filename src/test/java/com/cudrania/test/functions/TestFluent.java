package com.cudrania.test.functions;

import com.cudrania.core.functions.Fluent;
import com.cudrania.core.functions.Fn;
import com.cudrania.core.functions.Fn.Consumer;
import com.cudrania.core.functions.Params;
import com.cudrania.core.functions.Params.ImmutableParam;
import com.cudrania.core.utils.StringUtils;
import com.cudrania.test.bean.Contact;
import com.cudrania.test.bean.Family;
import com.cudrania.test.bean.People;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.cudrania.core.functions.Fluent.of;
import static com.cudrania.core.functions.Params.gt0;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author scorpio
 * @version 1.0.0
 */
public class TestFluent {


    @Test
    public void testBindMethod() {
        System.out.println(
                of(new HashMap<String, String>())
                        .accept(Map::put, "a", "Alex")
                        .accept(Map::put, "b", "Brown")
                        .accept(Map::put, "c", "Charles")
                        .accept(Map::put, "d", "Darwin")
                        .get()
        );
        System.out.println(
                of(new HashMap<String, String>())
                        .<String, String>consumer(Map::put)
                        .accept("a", "Alex")
                        .accept("b", "Brown")
                        .accept("c", "Charles")
                        .accept("d", "Darwin")
                        .consumer(System.out::println)
                        .accept()
                        .<String>consumer(Map::remove)
                        .accept("b")
                        .consumer(System.out::println)
                        .accept()
                        .function(Map::get)
                        .apply("d")
                        .get()
        );

        System.out.println(
                of(new People())
                        .consumer(People::setName)
                        .accept("name")
                        .consumer(People::setId)
                        .accept(1001L)
                        .consumer((Consumer<People>) System.out::println)
                        .accept()
                        .function(People::getId)
                        .apply()
                        .accept(System.out::println)
                        .get()
        );

        System.out.println(of(new Family())
                .accept(Family::setAddress, "a")
                .accept(f -> f.setAddress("b"))
                .accept(System.out::println)
                .get());
    }


    @Test
    public void testParam() {

        ImmutableParam<List> param = Params.with(10).then(n -> {
            System.out.println("2.====");
            return n * n;
        }).when(n -> {
            System.out.println("3.====");
            return n < 10;
        }).then(n -> {
            System.out.println("4.======");
            return "(" + n + ")";
        }).then(s -> new ArrayList(Arrays.asList(s)));
        System.out.println("1.======");
        System.out.println(param.get());
    }

    @Test
    public void testSimple() {
        System.out.println(
                of(new Family()).apply(Family::getHost).apply(People::getContact).apply(Contact::getAddress).get()
        );
        System.out.println(
                of((Family) null).apply(Family::getHost).apply(People::getContact).apply(Contact::getAddress).get()
        );
    }

    @Test
    public void testAll() {


        People people = of(new People())
                .accept(People::setId, 101L)
                .consumer(People::setName)
                .accept("name")
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

        assertAll(
                () -> {
                }
        );
        assertEquals(101L, people.getId());
        assertEquals("137****", people.getContact().getTelephone());
        assertEquals(100,
                Fluent.of(people)
                        .accept(gt0(100).then(id -> id.longValue()), People::setId)
                        .apply(People::getId).get());


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
                .<String>consumer(StringBuilder::append)
                .accept(Params.with(id).when(e -> e > 0).then(e -> e.toString()))
                .accept(Params.notEmpty(name))
                .accept(Params.notEmpty(email))
                .get().toString();
    }


}
