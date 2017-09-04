package com.cudrania.testsuite;

import com.nianien.core.util.ArrayUtils;
import com.nianien.core.util.StringUtils;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 定义SQL加载规则,在UT启动前自动加载SQL文件<br/>
 * 默认加载classpath:db_init目录下所有文件<br/>
 * 配合{@link SqlScripts}注解使用,可以实现在类或方法级别上加载SQL文件
 *
 * @author skyfalling.
 * @see SqlScripts
 */
public class SqlScriptLoader implements TestRule {

    /**
     * 默认加载目录
     */
    public static final String DEFAULT_LOADER_DIR = "db_init";
    /**
     * SQL文件目录
     */
    private String sqlDir = DEFAULT_LOADER_DIR;
    /**
     * SQL文件列表
     */
    private String[] sqlScripts = new String[0];

    /**
     * SQL文件目录
     *
     * @return 文件目录
     */
    public String sqlDir() {
        return sqlDir;
    }

    /**
     * SQL文件列表
     *
     * @return 文件列表
     */
    public String[] sqlScripts() {
        return sqlScripts;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        Set<String> set = new LinkedHashSet<>();
        SqlScripts scriptOnClass = AnnotationUtils.findAnnotation(description.getTestClass(), SqlScripts.class);
        if (scriptOnClass != null) {
            if (StringUtils.isNotEmpty(scriptOnClass.dir())) {
                this.sqlDir = scriptOnClass.dir();
            }
            //测试类上指定SQL脚本
            if (ArrayUtils.isNotEmpty(scriptOnClass.value())) {
                set.addAll(Arrays.asList(scriptOnClass.value()));
            }
        }
        SqlScripts scriptOnMethod = description.getAnnotation(SqlScripts.class);
        if (scriptOnMethod != null) {
            if (StringUtils.isNotEmpty(scriptOnMethod.dir())) {
                this.sqlDir = scriptOnMethod.dir();
            }
            //测试方法上指定SQL脚本
            if (ArrayUtils.isNotEmpty(scriptOnMethod.value())) {
                //是否扩展类上指定的SQL脚本
                if (!scriptOnMethod.append()) {
                    set.clear();
                }
                set.addAll(Arrays.asList(scriptOnMethod.value()));
            }
        }
        this.sqlScripts = set.toArray(new String[0]);
        return base;
    }

}
