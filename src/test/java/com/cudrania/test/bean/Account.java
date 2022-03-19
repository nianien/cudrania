package com.cudrania.test.bean;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author skyfalling
 */
@Data
@NoArgsConstructor
//@JsonFilter("sec-filter")
public class Account {

    private long id;

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

    public Account(long id, String userName) {
        this.id = id;
        this.userName = userName;
    }
}
