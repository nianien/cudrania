package com.cudrania.side.testsuite;

import com.cudrania.core.arrays.ArrayUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 在每个UT的Setup前自动加载SQL文件<p>
 * 配合{@link SqlScripts}注解使用,可以实现在类或方法级别上加载SQL文件
 *
 * @author skyfalling.
 * @see SqlScripts
 */
public class SqlScriptLoader implements BeforeEachCallback {


    @Override
    public void beforeEach(ExtensionContext context) {
        Collection<String> sqlScripts = new LinkedHashSet<>();
        SqlScripts scriptOnClass = AnnotationUtils.findAnnotation(context.getRequiredTestClass(), SqlScripts.class);
        if (scriptOnClass != null) {
            //测试类上指定SQL脚本
            if (ArrayUtils.isNotEmpty(scriptOnClass.value())) {
                sqlScripts.addAll(Arrays.asList(scriptOnClass.value()));
            }
        }
        SqlScripts scriptOnMethod = AnnotationUtils.findAnnotation(context.getRequiredTestMethod(), SqlScripts.class);
        if (scriptOnMethod != null) {
            //测试方法上指定SQL脚本
            if (ArrayUtils.isNotEmpty(scriptOnMethod.value())) {
                //是否扩展类上指定的SQL脚本
                if (!scriptOnMethod.append()) {
                    sqlScripts.clear();
                }
                sqlScripts.addAll(Arrays.asList(scriptOnMethod.value()));
            }
        }
        executeSql(sqlScripts, context);
    }

    /**
     * 通过Spring执行SQL
     *
     * @param sqlScripts
     * @param context
     */
    private void executeSql(Collection<String> sqlScripts, ExtensionContext context) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setContinueOnError(false);
        for (String sqlScript : sqlScripts) {
            populator.addScript(new ClassPathResource(sqlScript));
        }
        DataSource dataSource = SpringExtension.getApplicationContext(context).getBean(DataSource.class);
        DatabasePopulatorUtils.execute(populator, dataSource);
    }
}
