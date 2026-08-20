package com.nianien.cudrania.test.pipeline;

import com.nianien.cudrania.test.bean.Account;
import com.nianien.cudrania.test.bean.Home;
import com.nianien.cudrania.test.bean.User;

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

