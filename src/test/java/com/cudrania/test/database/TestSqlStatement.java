package com.cudrania.test.database;

import com.cudrania.core.date.DateFormatter;
import com.cudrania.core.utils.StringUtils;
import com.cudrania.idea.jdbc.sql.SqlStatement;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.cudrania.core.functions.Params.$;
import static com.cudrania.core.functions.Params.notEmpty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author skyfalling
 */
public class TestSqlStatement {


    @Test
    public void testStatement() {
        SqlStatement sqlStatement1 = new SqlStatement("select * from users where (userName,password) in ?", Arrays.asList(new String[]{"userName1", "password1"}, new String[]{"userName2", "password2"}));
//        SqlStatement sqlStatement2 = new SqlStatement("select * from users where (userName,password) in ?", new Object[][][]{{{"userName1", "password1"}, {"userName2", "password2"}}});
        SqlStatement sqlStatement2 = new SqlStatement("select * from users where (userName,password) in ?", new Object[]{new Object[][]{{"userName1", "password1"}, {"userName2", "password2"}}});
        System.out.println(sqlStatement1.expandSql());
        System.out.println(sqlStatement2.expandSql());
        assertThat(sqlStatement1.expandSql(), equalTo(sqlStatement2.expandSql()));
        SqlStatement sqlStatement3 = new SqlStatement()
                .append("select * from users where (userName,password) in ?",
                        $(new Object[][]{{"userName1", "password1"}, {"userName2", "password2"}}));
        System.out.println(sqlStatement3.expandSql());
    }


    @Test
    public void testSqlFunc() {
        List<String> names = Arrays.asList(new String[]{"a", "b", "c"});
        SqlStatement sql = new SqlStatement("select * from user where 1=1");
        sql.append(" name in ? ", notEmpty(names));
        System.out.println(sql.expandSql());

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
                .append("and name=:0", name)
                .append("and desc=':0' and (age>:0 and age<2*:0) and date in :1", age, dates)
                .appendIf("--注释", true);
        assertThat(sqlStatement.originalSql(), equalTo("select * from user where 1=1 and name=:0 and desc=':0' and (age>:0 and age<2*:0) and date in :1 --注释"));
        assertThat(sqlStatement.preparedSql(), equalTo("select * from user where 1=1 and name=? and desc=':0' and (age>? and age<2*?) and date in (?,?) --注释"));
        assertThat(sqlStatement.expandSql(), equalTo("select * from user where 1=1 and name='" + StringUtils.defaultIfNull(name, "") + "' and desc=':0' and (age>28 and age<2*28) and date in ('2014-02-08 10:00:00','" + DateFormatter.format(now) + "') --注释"));
        System.out.println(sqlStatement.expandSql());
        System.out.println(Arrays.toString(sqlStatement.preparedParameters()));

    }


}
