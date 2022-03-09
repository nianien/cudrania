package com.cudrania.test.database;

import com.cudrania.core.collection.wrapper.MapWrapper;
import com.cudrania.idea.jdbc.datasource.DataSourceBuilder;
import com.cudrania.idea.jdbc.query.SqlQueryBuilder;
import com.cudrania.idea.jdbc.sql.SqlGenerator;
import com.cudrania.idea.jdbc.sql.SqlStatement;
import com.cudrania.test.bean.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author skyfalling
 */
public class TestDataBase {


    private static SqlQueryBuilder queryBuilder;

    @BeforeAll
    public static void setUpAll() {
        Map<String, Object> map = new MapWrapper<String, Object>()
                .with("driverClass", "org.h2.Driver")
                .with("type", com.zaxxer.hikari.HikariDataSource.class)
                .with("jdbcUrl", "jdbc:h2:mem:test")
                .with("user", "sa")
                .with("password", "sa");
        DataSourceBuilder builder = new DataSourceBuilder();
        builder.addProperties(map);
        queryBuilder = new SqlQueryBuilder(builder.build());
        queryBuilder.build(new SqlStatement("CREATE TABLE `users`\n" +
                "(`uuid` BIGINT(20)  NOT NULL AUTO_INCREMENT,\n" +
                " `user_id` VARCHAR(1024)  NOT NULL DEFAULT '-1' ,\n" +
                " `user_name` VARCHAR(1024) NOT NULL DEFAULT '',\n" +
                " `password`  VARCHAR(1024) NOT NULL DEFAULT '',\n" +
                " PRIMARY KEY (`uuid`)\n" +
                ")\n")).executeUpdate();
    }

    @Test
    public void testQuery() {
        User user = new User();
        user.setId(1);
        List<User> list = queryBuilder.build().create(SqlGenerator.selectSql(user)).getRows(User.class);
        for (User user1 : list) {
            System.out.println(user1);
        }
    }

    @Test
    public void testInsert() {
        testQuery();
        User u = new User();
        u.setId(5);
        u.setUserId("test");
        u.setPassword("test");
        u.setUserName("落地飞天");
        queryBuilder.build().insert(u);
        testQuery();
    }

    @Test
    public void testUpdate() {
        testQuery();
        User u = new User();
        u.setId(5);
        u.setUserId("test");
        u.setPassword("test");
        u.setUserName("test");
        queryBuilder.build().update(u, "uuid");
        testQuery();
    }

    @Test
    public void testDelete() {
        testQuery();
        User u = new User();
        u.setId(5);
        u.setUserId("test");
        u.setPassword("test");
        u.setUserName("test");
        queryBuilder.build().delete(u, "uuid");
        testQuery();
    }

    @Test
    public void testBase() {
        String sql = "select * from users where (user_id,user_name) in :p";
        SqlStatement sqlStatement = new SqlStatement(sql, new MapWrapper("p", new Object[]{new String[]{"nianien", "落地飞天"}, new String[]{"wuhao1", "wuhao1"}}));
        System.out.println(sqlStatement.preparedSql());
        assertEquals(sqlStatement.preparedSql(), ("select * from users where (user_id,user_name) in ((?,?),(?,?))"));
        List<Map<String, Object>> list = queryBuilder.build(sqlStatement).getRows();
        for (Map<String, Object> map : list) {
            System.out.println(map);
        }
    }


}
