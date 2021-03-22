package com.cudrania.test.bean;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author skyfalling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Student extends People {

    private String school;
    private String major;
    private Date admissionTime;
    private Date graduationTime;

}
