package com.nianien.cudrania.test.verify;

import com.nianien.cudrania.core.arrays.ArrayUtils;
import com.nianien.cudrania.core.collection.map.DuoMap;
import java.util.HashMap;
import com.nianien.cudrania.core.math.SmartDecimal;
import com.nianien.cudrania.core.proxy.Interceptor;
import com.nianien.cudrania.core.proxy.ProxyFactory;
import com.nianien.cudrania.core.reflection.Reflections;
import com.nianien.cudrania.core.utils.StringUtils;
import com.nianien.cudrania.jdbc.datasource.DataSourceBuilder;
import com.nianien.cudrania.jdbc.query.SqlQueryBuilder;
import com.nianien.cudrania.jdbc.sql.SqlStatement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 第四批验证：整体复查中新发现问题的修复(含我上一轮引入的回归)。
 */
public class TestFixVerification4 {

    // ---- StringUtils ----
    @Test
    public void testLastIndexOfAny() {
        assertEquals(3, StringUtils.lastIndexOfAny("abca", "a"), "应返回最后一次出现的索引");
    }

    @Test
    public void testCombineSpecialConnector() {
        // 连接符含$, 且产生连续重复, 修复前replaceAll替换串未转义会抛异常
        assertDoesNotThrow(() -> StringUtils.combine("$", "a", "", "b"));
    }

    // ---- ArrayUtils.subtract 空第二数组 ----
    @Test
    public void testSubtractEmptySecond() {
        assertArrayEquals(new int[]{1, 2, 3}, ArrayUtils.subtract(new int[]{1, 2, 3}, new int[]{}));
        assertArrayEquals(new int[]{1, 3}, ArrayUtils.subtract(new int[]{1, 2, 3}, new int[]{2}));
        assertArrayEquals(new int[0], ArrayUtils.subtract(new int[]{}, new int[]{1}));
    }

    // ---- SmartDecimal equals/hashCode 一致 ----
    @Test
    public void testSmartDecimalHashCode() {
        SmartDecimal a = new SmartDecimal(new BigDecimal("1.0"), 4, java.math.RoundingMode.HALF_UP);
        SmartDecimal b = new SmartDecimal(new BigDecimal("1.00"), 4, java.math.RoundingMode.HALF_UP);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode(), "equals为true时hashCode必须相同");
        Map<SmartDecimal, String> map = new HashMap<>();
        map.put(a, "x");
        assertEquals("x", map.get(b), "等值不同scale的键应能命中");
    }

    // ---- DuoMap.updateValue 返回旧值 ----
    @Test
    public void testDuoMapUpdate() {
        DuoMap<String, Integer, Integer> m = new DuoMap<>();
        assertNull(m.updateValue1("k", 10), "不存在的key更新应返回null");
        assertEquals(10, m.updateValue1("k", 20), "已存在key更新应返回旧值");
    }

    // ---- ProxyFactory 解包InvocationTargetException ----
    public interface Boomer {
        void boom();
    }

    public static class Svc implements Boomer {
        public void boom() {
            throw new IllegalStateException("boom!");
        }
    }

    @Test
    public void testProxyUnwrapsException() {
        final Exception[] captured = new Exception[1];
        Interceptor interceptor = new Interceptor() {
            public void before(Object t, Method m, Object... a) {
            }

            public void after(Object t, Method m, Object... a) {
            }

            public Object exception(Exception ex, Object t, Method m, Object... a) {
                captured[0] = ex;
                return null;
            }
        };
        Boomer proxy = (Boomer) ProxyFactory.proxy(new Svc(), interceptor);
        proxy.boom();
        assertTrue(captured[0] instanceof IllegalStateException,
                "拦截器应收到真实异常而非InvocationTargetException: " + captured[0]);
        assertEquals("boom!", captured[0].getMessage());
    }

    // ---- getMethod 不因不兼容重载而误报ambiguous ----
    public static class Over {
        public String f(CharSequence s) {
            return "cs";
        }

        public String f(Integer i) {
            return "int";
        }
    }

    @Test
    public void testGetMethodNoFalseAmbiguous() {
        Method m = Reflections.getMethod(Over.class, "f", String.class);
        assertNotNull(m);
        assertEquals(CharSequence.class, m.getParameterTypes()[0], "应选中兼容的f(CharSequence)而非报ambiguous");
    }

    // ---- DB: 可空的Integer列不应让整个查询崩溃(上一轮回归) ----
    static SqlQueryBuilder qb;

    @BeforeAll
    public static void setUp() {
        Map<String, Object> map = new HashMap<>();
        map.put("driverClass", "org.h2.Driver");
        map.put("type", com.zaxxer.hikari.HikariDataSource.class);
        map.put("jdbcUrl", "jdbc:h2:mem:verify4;MODE=MySQL");
        map.put("user", "sa");
        map.put("password", "sa");
        DataSourceBuilder b = new DataSourceBuilder();
        b.addProperties(map);
        qb = new SqlQueryBuilder(b.build());
        qb.build(new SqlStatement("CREATE TABLE t (id BIGINT NOT NULL, age INT, PRIMARY KEY(id))")).executeUpdate();
        qb.build().create(new SqlStatement("insert into t(id, age) values(1, null)")).executeUpdate();
    }

    public static class Row {
        private Long id;
        private Integer age;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    @Test
    public void testNullableWrapperColumn() {
        List<Row> rows = qb.build().create(new SqlStatement("select id, age from t")).getRows(Row.class);
        assertEquals(1, rows.size());
        assertEquals(1L, rows.get(0).getId());
        assertNull(rows.get(0).getAge(), "NULL的Integer列应映射为null, 且不应让查询崩溃");
    }
}
