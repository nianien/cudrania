package com.cudrania.side.testsuite;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 定义SQL文件加载选项
 *
 * @author skyfalling.
 */
@Target({METHOD, TYPE})
@Retention(RUNTIME)
public @interface SqlScripts {

    /**
     * 指定SQL脚本列表
     *
     * @return
     */
    String[] value() default {};


    /**
     * 是否追加SQL,默认不追加<p>
     *
     * @return true表示加载继承以及当前SQL列表, false则仅加载当前SQL列表
     */
    boolean append() default false;
}
