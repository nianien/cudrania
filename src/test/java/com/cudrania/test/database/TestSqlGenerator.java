package com.cudrania.test.database;

import com.cudrania.core.collection.wrapper.MapWrapper;
import com.cudrania.idea.jdbc.sql.SqlGenerator;
import com.cudrania.idea.jdbc.sql.SqlStatement;
import com.cudrania.test.bean.User;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

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
        sqlStatement = SqlGenerator.updateSql(user);

        assertThat(sqlStatement.renderSql(), equalTo("update users set password = '' , userId = 'skyfalling' , userName = 'who' , uuid = 1 where uuid = 1"));
        sqlStatement = SqlGenerator.updateSql(user, (String[]) null);
        assertThat(sqlStatement.renderSql(), equalTo("update users set password = '' , userId = 'skyfalling' , userName = 'who' , uuid = 1"));


        sqlStatement = SqlGenerator.deleteSql(user);
        assertThat(sqlStatement.renderSql(), equalTo("delete from users where userId = 'skyfalling' and userName = 'who' and uuid = 1"));
        sqlStatement = SqlGenerator.deleteSql(user, (String[]) null);
        assertThat(sqlStatement.renderSql(), equalTo("delete from users"));


        sqlStatement = SqlGenerator.insertSql(user);
        assertThat(sqlStatement.renderSql(), equalTo("insert into users (userId,userName,uuid) values('skyfalling','who',1)"));

        sqlStatement = SqlGenerator.selectSql(user);
        assertThat(sqlStatement.renderSql(), equalTo("select * from users where userId = 'skyfalling' and userName = 'who' and uuid = 1"));
        sqlStatement = SqlGenerator.selectSql(user, (String[]) null);
        assertThat(sqlStatement.renderSql(), equalTo("select * from users"));

        sqlStatement = SqlGenerator.whereSql(new SqlStatement("select * from users"), user, "userId", "userName");
        assertThat(sqlStatement.renderSql(), equalTo("select * from users where userId = 'skyfalling' and userName = 'who'"));
        sqlStatement = SqlGenerator.whereSql(new SqlStatement("select * from users"), new MapWrapper<>("userId", "skyfalling").with("userName", "who"));
        assertThat(sqlStatement.renderSql(), equalTo("select * from users where userName = 'who' and userId = 'skyfalling'"));

    }



}
