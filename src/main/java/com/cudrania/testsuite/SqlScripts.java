package com.cudrania.testsuite;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 定义SQL文件加载选项
 *
 * @author skyfalling.
 */
@Target ({METHOD, TYPE})
@Retention (RUNTIME)
public @interface SqlScripts {

    /**
     * 指定SQL脚本列表,默认加载全部SQL脚本
     *
     * @return
     */
    String[] value() default {};

    /**
     * 指定SQL文件目录,默认值: {@link SqlScriptLoader#DEFAULT_LOADER_DIR}
     *
     * @return SQL文件目录
     */
    String dir() default SqlScriptLoader.DEFAULT_LOADER_DIR;

    /**
     * 是否追加模式,默认不追加<br/>
     *
     * @return true表示将指定的SQL文件追加到当前列表中, false则仅加载指定的SQL文件
     */
    boolean append() default false;
}
