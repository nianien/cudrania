package com.nianien.cudrania.test.bean;

import com.nianien.cudrania.core.reflection.Ignore;
import com.nianien.cudrania.core.reflection.Property;
import com.nianien.cudrania.jdbc.table.Id;
import com.nianien.cudrania.jdbc.table.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author skyfalling
 */
@Table("users")
@Data
@NoArgsConstructor
public class User {

    private String userId;
    private String userName;
    private String password;
    @Property("uuid")
    @Id
    private int id;
    @Ignore
    private String[] userDesc;
    @Ignore
    private Map<String, String> extras;

    public User(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

}
