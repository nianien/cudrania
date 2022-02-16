package com.cudrania.test.bean;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.Map;

/**
 * @author skyfalling
 */
@Data
public class Account {

    private int id;

    private String userName;

    @JsonView({FullView.class})
    private String password;

    @JsonView({SimpleView.class})
    private String phone;

    private Map<String, String> extras;

    public interface FullView extends SimpleView {
    }

    public interface SimpleView {
    }

}
