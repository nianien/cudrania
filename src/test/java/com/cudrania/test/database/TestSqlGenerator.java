package com.cudrania.test.database;

import com.cudrania.core.collection.wrapper.MapWrapper;
import com.cudrania.jdbc.sql.SqlStatement;
import com.cudrania.test.bean.User;
import org.junit.jupiter.api.Test;

import static com.cudrania.jdbc.sql.SqlGenerator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author skyfalling
 */
public class TestSqlGenerator {


    @Test
    public void testEntityBuilder() {
        User user = new User();
        user.setId(1);
        user.setUserName("who");
        user.setUserId("skyfalling");
        user.setUserDesc(new String[]{"test1", "desc2"});
        SqlStatement sqlStatement;
        sqlStatement = updateSql(user);

        assertEquals("update users set user_id = 'skyfalling' , user_name = 'who' where uuid = 1", sqlStatement.renderSql());


        sqlStatement = deleteSql(user);
        assertEquals("delete from users where user_id = 'skyfalling' and user_name = 'who' and uuid = 1", sqlStatement.renderSql());


        sqlStatement = insertSql(user);
        assertEquals("insert into users (user_id,user_name,uuid) values('skyfalling','who',1)", sqlStatement.renderSql());

        sqlStatement = selectSql(user);
        assertEquals("select * from users where user_id = 'skyfalling' and user_name = 'who' and uuid = 1", sqlStatement.renderSql());

        sqlStatement = new SqlStatement("select * from users where").append(getFields(user, "user_id", "user_name"));
        assertEquals("select * from users where user_id = 'skyfalling' and user_name = 'who'", sqlStatement.renderSql());
        sqlStatement = new SqlStatement("select * from users where").append(new MapWrapper<>("user_id", "skyfalling").with("user_name", "who"));
        assertEquals("select * from users where user_id = 'skyfalling' and user_name = 'who'", sqlStatement.renderSql());


    }


}
