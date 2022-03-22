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
                // .begin(Long.TYPE,String.class,String.class)
                .begin(Names::input1, Names::input2, Names::input3)
                .with(Names::input1, Names::input2)
                .and(allAbilities::createAccount)
                .as(Names::account)
                .and(allAbilities::createUser)
                .as(Names::user1)
                // .<Account>with("account")
                .with(Names::account)
                .and(allAbilities::createUser)
                .as(Names::user2)
                .with(Names::input3, Names::user1, Names::user2)
                .and(allAbilities::createHome)
                .end();


        System.out.println(pipeline.eval(1000001L, "jack.wang", "china.beijing"));


    }


//    public void testPipeline2() {
//
//        AllAbilities allAbilities = new AllAbilities();
//        Pipeline2<Long,String> pipeline =
//        PipelineImpl.begin(Names::input1, Names::input2)
//                .and(allAbilities::createAccount)
//                .as(Names::account)
//                .and(allAbilities::createUser)
//                .as(Names::user1)
//                .with(Names::account)
//                .and(allAbilities::createUser)
//                .as(Names::user2)
//                .with(Names::user1, Names::user2)
//                .and((u1, u2) -> allAbilities.createHome("test", u1, u2))
//                .end();
//
//
//        Account account = pipeline.eval(1L,"");
//        System.out.println(account);
//
//    }

    /**
     * 用于节点命名
     */
    public interface Names {
        Long input1();

        String input2();

        String input3();

        User user1();

        User user2();

        Account account();

        User output();
    }

}
