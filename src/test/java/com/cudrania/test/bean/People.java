package com.cudrania.test.bean;

import java.util.Date;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * User: lining05
 * Date: 2013-01-31
 */
@Data
public class People {
    private Long id;
    private String name;
    private Date birthday;
    private String sex;
    private Contact contact;
}
