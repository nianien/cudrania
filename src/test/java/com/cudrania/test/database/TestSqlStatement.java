package com.cudrania.test.database;

import com.cudrania.core.date.DateFormatter;
import com.cudrania.core.functions.Params;
import com.cudrania.idea.jdbc.sql.SqlStatement;
import org.junit.Test;

import java.util.*;

import static com.cudrania.core.functions.Params.notNull;
import static com.cudrania.core.functions.Params.with;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author skyfalling
 */
public class TestSqlStatement {


    @Test
    public void testStatement() {
        SqlStatement sqlStatement1 = new SqlStatement("select * from users where 1=1")
                .append("and (userName,password) in ?", Arrays.asList(new String[]{"userName1", "password1"}, new String[]{"userName2", "password2"}));
        SqlStatement sqlStatement2 = new SqlStatement("select * from users where 1=1")
                .append("and (userName,password) in ?", new Object[]{new Object[][]{{"userName1", "password1"}, {"userName2", "password2"}}});
        System.out.println(sqlStatement1.renderSql());
        System.out.println(sqlStatement2.renderSql());
        assertThat(sqlStatement1.renderSql(), equalTo(sqlStatement2.renderSql()));
        SqlStatement sqlStatement3 = new SqlStatement("select * from users where 1=1")
                .append("and (userName,password) in ?", notNull(new Object[][]{{"userName1", "password1"}, {"userName2", "password2"}})
                        .when(e -> e.length > 1));
        System.out.println(sqlStatement3.renderSql());
    }


    @Test
    public void testSqlFunc() {
        List<String> names = Arrays.asList(new String[]{"a", "b", "c"});
        Map map = new HashMap();
//        map.put("type", "special");
        SqlStatement sql = new SqlStatement("select * from user where 1=1")
                .append("and type=:type", with(map).when(m -> !m.isEmpty()))
                .append("and name in ? and alias in :0 and type=:type", names, map);
        System.out.println(sql.renderSql());
        System.out.println(sql.preparedSql());

    }

    @Test
    public void testSql() {
        String name = "";
        int age = 28;
        //MM-dd-yyyy HH:mm:ss
        Date now = new Date();
        Object[] dates = new Object[]{"2014-02-08 10:00:00", now};
        SqlStatement sqlStatement = new SqlStatement();
        sqlStatement.append("select * from user where 1=1")
                .append("and name=:0", Params.notEmpty(name))
                .append("and desc=':0' and (age>:0 and age<2*:0) and date in :1", age, dates)
                .append("--注释");
        assertThat(sqlStatement.originalSql(), equalTo("select * from user where 1=1 and desc=':0' and (age>:0 and age<2*:0) and date in :1 --注释"));
        assertThat(sqlStatement.preparedSql(), equalTo("select * from user where 1=1 and desc=':0' and (age>? and age<2*?) and date in (?,?) --注释"));
        assertThat(sqlStatement.renderSql(), equalTo("select * from user where 1=1 and desc=':0' and (age>28 and age<2*28) and date in ('2014-02-08 10:00:00','" + DateFormatter.format(now) + "') --注释"));
        System.out.println(sqlStatement.renderSql());
        System.out.println(Arrays.toString(sqlStatement.preparedParameters()));

    }


}
