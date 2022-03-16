package com.cudrania.test.bean;

import com.cudrania.core.reflection.Ignore;
import com.cudrania.core.reflection.Property;
import com.cudrania.jdbc.table.Id;
import com.cudrania.jdbc.table.Table;
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

    public User setUserId2(String userId) {
        this.userId = userId;
        return this;
    }
}
