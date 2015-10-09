package com.cudrania.testsuite;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 数据库操作的UT套件,支持自动加载SQL脚本<br/>
 * 示例:
 * <pre>
 * <code>&nbsp;@SqlScripts(value = {"schema.sql","default.sql"})
 * &nbsp;@ContextConfiguration(locations = "classpath:applicationContext.xml")
 *   public class DataBaseServiceTest extends DBUnit4SpringContextTests {
 *
 *       &nbsp;@Test
 *         public void test1() {
 *             //auto load "schema.sql","default.sql" before test1() executed
 *         }
 *
 *       &nbsp;@Test
 *       &nbsp;@SqlScripts(value = {"another.sql"})
 *         public void test2() {
 *             //auto load "another.sql" before test2() executed
 *         }
 *
 *       &nbsp;@Test
 *       &nbsp;@SqlScripts(value = {"another.sql"},append=true)
 *         public void test3() {
 *             //auto load "schema.sql","default.sql","another.sql" before test3() executed
 *         }
 * &nbsp;}
 * </code>
 * </pre>
 *
 * @author skyfalling .
 */
@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations = {"classpath*:applicationContext*.xml"})
public abstract class DBUnit4SpringContextTests extends AbstractTransactionalJUnit4SpringContextTests {

    @Rule
    public SqlScriptLoader sqlScriptLoader = new SqlScriptLoader();

    @Before
    public void setUp() throws Exception {
        for (String sqlScript : initSqlScripts()) {
            executeSqlScript(sqlScript, false);
        }
    }

    /**
     * 获取SQL文件列表
     *
     * @return SQL文件列表
     *
     * @throws IOException 读SQL文件失败时抛出该异常
     */
    protected String[] initSqlScripts() throws IOException {
        final List<String> sqlScripts = new ArrayList<String>();
        if (ArrayUtils.isNotEmpty(sqlScriptLoader.sqlScripts())) {
            for (String file : sqlScriptLoader.sqlScripts()) {
                join(sqlScripts, file);
            }
        } else {
            File dir = this.applicationContext.getResource(sqlScriptLoader.sqlDir()).getFile();
            dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.toLowerCase().endsWith(".sql")) {
                        join(sqlScripts, name);
                    }
                    return false;
                }
            });
        }
        return sqlScripts.toArray(new String[0]);
    }

    /**
     * 合并文件列表,并去重
     *
     * @param sqlScripts SQL文件列表
     * @param name       待加入的SQL文件
     */
    private void join(List<String> sqlScripts, String name) {
        String script = StringUtils.isEmpty(sqlScriptLoader.sqlDir()) ? name : sqlScriptLoader.sqlDir() + "/" + name;
        if (name.equalsIgnoreCase("schema.sql")) {
            sqlScripts.add(0, script);
        } else {
            sqlScripts.add(script);
        }
    }

}
