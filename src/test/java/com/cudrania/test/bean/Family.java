package com.cudrania.test.bean;

import java.util.Map;

import lombok.Data;

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

}
