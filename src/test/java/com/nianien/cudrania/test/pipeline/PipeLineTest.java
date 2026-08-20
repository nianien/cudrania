package com.nianien.cudrania.test.pipeline;


import com.nianien.cudrania.core.pipeline.Named;
import com.nianien.cudrania.core.pipeline.Pipeline;
import com.nianien.cudrania.core.pipeline.Pipeline2;
import com.nianien.cudrania.core.pipeline.Pipeline3;
import com.nianien.cudrania.core.pipeline.Pipelines;
import com.nianien.cudrania.test.bean.Account;
import com.nianien.cudrania.test.bean.Home;
import com.nianien.cudrania.test.bean.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PipeLineTest {


    @Test
    public void test() {

        AllAbilities allAbilities = new AllAbilities();
        Pipeline3<Long, String, String, Home> pipeline = Pipelines
                // .of(Long.TYPE, String.class, String.class)
                .of(Inputs::input1, Inputs::input2, Inputs::input3)
                .with(Inputs::input1, Inputs::input2)
                .and(allAbilities::createAccount)
                .as(Outputs::account)
                .and(allAbilities::createUser)
                .as(Outputs::user1)
                // .<Account>with("account")
                .with(Outputs::account)
                .and(allAbilities::createUser)
                .as(Outputs::user2)
                .with(Inputs::input3, Outputs::user1, Outputs::user2)
                .and(allAbilities::createHome)
                .end();
        Home home = pipeline.eval(1000001L, "jack.wang", "china.beijing");
        System.out.println(home);
        Assertions.assertEquals(home.getClass(), Home.class);
        Assertions.assertEquals(home.getAddress(), "address:china.beijing");
        Assertions.assertEquals(home.getUsers().size(), 2);

    }

    @Test
    public void testSingleInputPipeline() {
        Pipeline<Integer, String> pipeline = Pipelines.of(Integer.class)
                .and(value -> "value:" + value)
                .end();

        Assertions.assertEquals("value:10", pipeline.eval(10));
    }

    @Test
    public void testTwoInputPipeline() {
        Pipeline2<Integer, Integer, Integer> pipeline = Pipelines.of(Integer.class, Integer.class)
                .and(Integer::sum)
                .end();

        Assertions.assertEquals(30, pipeline.eval(10, 20));
    }

    @Test
    public void testStringInputAndFailOver() {
        Named<Inputs, String> input = Inputs::input2;
        Pipeline<String, Integer> pipeline = Pipelines.of(input)
                .<String>with(input.name())
                .and(Integer::parseInt)
                .failOver(error -> -1)
                .end();

        Assertions.assertEquals(-1, pipeline.eval("invalid"));
    }


    /**
     * 参数输入
     */
    public interface Inputs {

        Long input1();

        String input2();

        String input3();

    }

    /**
     * 存储结果
     */
    public interface Outputs {

        User user1();

        User user2();

        Account account();

    }

}
