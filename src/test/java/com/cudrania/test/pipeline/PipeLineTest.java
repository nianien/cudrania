package com.cudrania.test.pipeline;


import com.cudrania.core.pipeline.Pipeline3;
import com.cudrania.core.pipeline.Pipelines;
import com.cudrania.test.bean.Account;
import com.cudrania.test.bean.Home;
import com.cudrania.test.bean.User;

public class PipeLineTest {


    public static void main(String[] args) {


        AllAbilities allAbilities = new AllAbilities();

        Pipeline3<Long, String, String, Home> pipeline = Pipelines
//                .of(Long.TYPE, String.class, String.class)
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
