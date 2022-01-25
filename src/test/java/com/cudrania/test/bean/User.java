package com.cudrania.test.bean;

import com.cudrania.core.annotation.Ignore;
import com.cudrania.core.annotation.Property;
import com.cudrania.idea.jdbc.table.Column;
import com.cudrania.idea.jdbc.table.Id;
import com.cudrania.idea.jdbc.table.Table;

import java.sql.JDBCType;
import java.util.Map;

/**
 * @author skyfalling
 */
@Table("users")
public class User {

    private int id;
    private String userId, userName, password;
    private String[] userDesc;
    private Map<String, String> extras;

    public User() {
    }

    /**
     * @return
     */
    @Property("uuid")
    @Id
    public int getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Ignore
    @Column(value = "desc", sqlType = JDBCType.VARCHAR)
    public String[] getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String[] userDesc) {
        this.userDesc = userDesc;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }


}
