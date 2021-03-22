package com.cudrania.idea.jdbc.table;

import java.lang.annotation.*;

/**
 * 标记键值字段的注解
 *
 * @author skyfalling
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Id {
}
