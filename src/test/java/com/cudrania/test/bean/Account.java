package com.cudrania.test.bean;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.Map;

/**
 * @author skyfalling
 */
@Data
//@JsonFilter("sec-filter")
public class Account {

    private int id;

    private String userName;

    @JsonView({FullView.class})
    private String password;

    @JsonView({SimpleView.class})
    private String phone;

//    @JsonFilter("sec-filter")
    private Map<String, String> extras;

    public interface FullView extends SimpleView {
    }

    public interface SimpleView {
    }

}
