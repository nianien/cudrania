package com.cudrania.test.pipeline;

import com.cudrania.test.bean.Account;
import com.cudrania.test.bean.Home;
import com.cudrania.test.bean.User;

public class AllAbilities {

    public Account createAccount(Long id, String name) {
        return new Account(id, name);
    }


    public User createUser(Account account) {
        return new User("u-" + account.getId(), account.getUserName());
    }


    public Home createHome(String address, User user1, User user2) {
        return new Home("address:" + address, user1, user2);
    }

}

