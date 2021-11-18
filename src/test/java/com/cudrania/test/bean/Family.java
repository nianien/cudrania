package com.cudrania.test.bean;

import lombok.Data;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lining05
 * Date: 2013-01-31
 */
@Data
public class Family {
    private String address;
    private People host;
    private Map<String, People> members;

    public Family() {
    }


    public People find(String name) {
        return members.get(name);
    }

}
