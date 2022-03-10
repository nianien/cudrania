package com.cudrania.idea.jdbc.table;

import com.cudrania.core.annotation.Property;
import com.cudrania.core.reflection.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.JDBCType;

import static com.cudrania.core.utils.StringUtils.isNotEmpty;
import static com.cudrania.core.utils.StringUtils.underscoreCase;

/**
 * 获取表名和字段名的辅助类
 *
 * @author skyfalling
 */
public class TableHelper {
    private TableHelper() {
    }

    /**
     * 根据实体类型获取数据库表名称<br>
     * 其中,注解名称优先于类名称
     *
     * @param clazz
     * @return 表名称
     */
    public static String getTableName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        String name = table != null ? table.value() : null;
        return isNotEmpty(name) ? name : underscoreCase(clazz.getSimpleName());
    }

    /**
     * 根据getter/setter(或isXXX)方法获取数据库表中字段名称<br>
     * 优先级: @Column>@Property>getter
     *
     * @param method
     * @return 字段名称
     */
    public static Column getColumnName(Method method, Field field) {
        Column column = method.getAnnotation(Column.class);
        if (column == null && field != null) {
            column = field.getAnnotation(Column.class);
        }
        if (column != null && !column.value().isEmpty()) {
            return column;
        }
        String name;
        if (!method.isAnnotationPresent(Property.class)
                && field != null
                && field.isAnnotationPresent(Property.class)) {
            name = underscoreCase(Reflections.propertyName(field));
        } else {
            name = underscoreCase(Reflections.propertyName(method));
        }
        return new Column() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Column.class;
            }

            @Override
            public String value() {
                return name;
            }

            @Override
            public JDBCType sqlType() {
                return JDBCType.JAVA_OBJECT;
            }
        };
    }


}
