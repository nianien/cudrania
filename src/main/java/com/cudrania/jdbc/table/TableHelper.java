package com.cudrania.jdbc.table;

import com.cudrania.core.reflection.BeanProperty;

import java.lang.annotation.Annotation;
import java.sql.JDBCType;
import java.util.Arrays;

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
     * 优先级: @Column &gt; @Property &gt; getter
     *
     * @param beanProperty
     * @return Column
     */
    public static Column getColumnName(BeanProperty beanProperty) {
        //取首个@Column(不再因value为空而整体丢弃, 否则只设sqlType的@Column会丢失sqlType)
        Column column = Arrays.stream(beanProperty.getAnnotations(Column.class)).findFirst().orElse(null);
        //列名: @Column.value非空则用之, 否则按字段名下划线化
        String name = column != null && !column.value().isEmpty()
                ? column.value() : underscoreCase(beanProperty.getAlias());
        //类型: 有@Column则沿用其sqlType(默认OTHER), 无注解时退回JAVA_OBJECT
        JDBCType type = column != null ? column.sqlType() : JDBCType.JAVA_OBJECT;
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
                return type;
            }
        };
    }


}
