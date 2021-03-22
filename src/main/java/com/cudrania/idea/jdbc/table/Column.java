package com.cudrania.idea.jdbc.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.JDBCType;

/**
 * 标记数据库字段名称的注解
 *
 * @author skyfalling
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Column {
    /**
     * 数据库字段名称
     *
     * @return 表名称
     */
    String value();

    /**
     * JDBC数据类型
     *
     * @return
     * @see java.sql.Types
     */
    JDBCType sqlType() default JDBCType.OTHER;
}
