package com.cudrania.test.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Data
public class Home {

    private String address;
    private List<User> users;


    public Home(String address, User... users) {
        this.address = address;
        this.users = Arrays.asList(users);
    }
}
