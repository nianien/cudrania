package com.cudrania.test.verify;

import com.cudrania.core.bits.MultiBitSet;
import java.util.HashMap;
import com.cudrania.jdbc.datasource.DataSourceBuilder;
import com.cudrania.jdbc.query.SqlQueryBuilder;
import com.cudrania.jdbc.sql.SqlStatement;
import com.cudrania.jdbc.table.Column;
import com.cudrania.jdbc.table.DataTable;
import com.cudrania.jdbc.table.DataTableFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.JDBCType;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 第三批验证：需要DB/注解的真实可观测改动。
 */
public class TestFixVerification3 {

    /**
     * MultiBitSet.nextSetBit在无匹配时返回-1(修复前bitNum>1时 -1/bitNum==0, 误判命中索引0)。
     */
    @Test
    public void testMultiBitSetNextSetBitNotFound() {
        MultiBitSet nb = new MultiBitSet(5);
        assertEquals(-1, nb.nextSetBit(0), "空集合nextSetBit应返回-1");
    }

    // --- TableHelper: @Column只设sqlType不设value时, sqlType不应丢失 ---
    static class Bean {
        @Column(value = "", sqlType = JDBCType.VARCHAR)
        private String userName;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

    /**
     * @Column只设sqlType(value为空)时, sqlType必须保留(修复前被filter丢弃, 退回JAVA_OBJECT)。
     */
    @Test
    public void testColumnSqlTypePreserved() {
        DataTable table = DataTableFactory.get(Bean.class);
        //value为空 → 列名按字段名下划线化为user_name
        assertEquals(JDBCType.VARCHAR, table.getField("user_name").getSqlType(),
                "只设sqlType的@Column应保留其sqlType");
    }

    // --- DB分页: beforeRow对TYPE_FORWARD_ONLY结果集的分页 ---
    static SqlQueryBuilder queryBuilder;

    @BeforeAll
    public static void setUp() {
        Map<String, Object> map = new HashMap<>();
        map.put("driverClass", "org.h2.Driver");
        map.put("type", com.zaxxer.hikari.HikariDataSource.class);
        map.put("jdbcUrl", "jdbc:h2:mem:verify;MODE=MySQL");
        map.put("user", "sa");
        map.put("password", "sa");
        DataSourceBuilder builder = new DataSourceBuilder();
        builder.addProperties(map);
        queryBuilder = new SqlQueryBuilder(builder.build());
        queryBuilder.build(new SqlStatement("CREATE TABLE t (id BIGINT NOT NULL, PRIMARY KEY(id))")).executeUpdate();
        for (int i = 1; i <= 5; i++) {
            queryBuilder.build().create(new SqlStatement("insert into t(id) values(?)", i)).executeUpdate();
        }
    }

    /**
     * getRows(start,size)对只进结果集分页: 从第2条起取2条应为id=2,3。
     * 修复前TYPE_FORWARD_ONLY分支的while无break, 会读到末尾, 返回空。
     */
    @Test
    public void testForwardOnlyPagination() {
        List<Map<String, Object>> rows = queryBuilder.build()
                .create(new SqlStatement("select id from t order by id")).getRows(2, 2);
        assertEquals(2, rows.size(), "应返回2条: " + rows);
        assertEquals(2L, ((Number) rows.get(0).get("id")).longValue());
        assertEquals(3L, ((Number) rows.get(1).get("id")).longValue());
    }
}
