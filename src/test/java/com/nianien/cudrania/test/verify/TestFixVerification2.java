package com.nianien.cudrania.test.verify;

import com.nianien.cudrania.core.io.Files;
import com.nianien.cudrania.core.reflection.Reflections;
import com.nianien.cudrania.core.text.Encoder;
import com.nianien.cudrania.core.text.MessageDigests;
import com.nianien.cudrania.core.tree.TrieTree;
import com.nianien.cudrania.jdbc.sql.SqlGenerator;
import com.nianien.cudrania.jdbc.sql.SqlOperator;
import com.nianien.cudrania.jdbc.sql.SqlStatement;
import com.nianien.cudrania.jdbc.sql.SqlTypeConverter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 第二批验证：覆盖剩余的行为性改动，硬断言。
 */
public class TestFixVerification2 {

    static class Parent {
        int pf;
    }

    static class Child extends Parent {
        int cf;
    }

    /**
     * Reflections.getFields必须包含父类字段(修复前循环变量用错, 父类字段全丢)。
     */
    @Test
    public void testReflectionsInheritedFields() {
        List<String> names = Reflections.getFields(Child.class).stream()
                .map(Field::getName).collect(Collectors.toList());
        assertTrue(names.contains("cf"), "应含本类字段cf: " + names);
        assertTrue(names.contains("pf"), "应含父类字段pf: " + names);
        // 每个字段只应出现一次(修复前会重复N次)
        assertEquals(1, Collections.frequency(names, "cf"));
        assertEquals(1, Collections.frequency(names, "pf"));
    }

    public static class Calc {
        public String go(Integer a) {
            return "one:" + a;
        }

        public String go(Integer a, Integer b) {
            return "two:" + (a + b);
        }

        public String prim(int a, long b) {
            return "prim:" + (a + b);
        }

        public String ref(String s) {
            return "ref:" + s;
        }
    }

    /**
     * Reflections.invoke按<b>参数个数</b>正确区分重载。
     * <p>
     * 修复前argsMatched写成{@code args.length != args.length}(恒false), 参数个数不匹配时不短路,
     * 会把单参调用错误匹配到双参重载。修复后按个数精确区分。
     */
    @Test
    public void testReflectionsInvokeArgMatch() {
        assertEquals("one:5", Reflections.invoke("go", new Calc(), 5));
        assertEquals("two:5", Reflections.invoke("go", new Calc(), 2, 3));
    }

    /**
     * Reflections.invoke对<b>基本类型形参</b>的方法可用(装箱实参匹配), 且null实参匹配引用形参。
     * 修复前argsMatched用 expected.isInstance(arg), 基本类型isInstance恒false, 完全调不到。
     */
    @Test
    public void testReflectionsInvokePrimitiveAndNull() {
        assertEquals("prim:5", Reflections.invoke("prim", new Calc(), 2, 3L));
        assertEquals("ref:null", Reflections.invoke("ref", new Calc(), new Object[]{null}));
    }

    /**
     * SqlStatement: 空集合应生成合法的(null)而非非法的 in ()。
     */
    @Test
    public void testSqlEmptyInList() {
        String sql = new SqlStatement("select * from t where id in ?", new Object[]{new ArrayList<>()}).renderSql();
        assertTrue(sql.contains("(null)"), "空集合应生成(null): " + sql);
        assertFalse(sql.matches(".*in\\s*\\(\\s*\\).*"), "不应出现空的in (): " + sql);
    }

    /**
     * SqlStatement.renderSql: 字符串值中的单引号必须转义。
     */
    @Test
    public void testSqlQuoteEscape() {
        String sql = new SqlStatement("select * from t where name=?", "O'Brien").renderSql();
        assertTrue(sql.contains("'O''Brien'"), "单引号应转义为'': " + sql);
    }

    /**
     * SqlOperator.toSQL: 列名含$/\不应被当作正则替换模板(修复前replaceFirst会出错)。
     */
    @Test
    public void testSqlOperatorSpecialName() {
        String sql = SqlOperator.Equal.toSQL("col$1");
        assertTrue(sql.contains("col$1"), "列名应原样出现: " + sql);
    }

    /**
     * SqlGenerator.insertSql空字段必须抛异常(修复前用assert, 生产环境默认关闭会生成非法SQL)。
     */
    @Test
    public void testInsertSqlEmptyThrows() {
        assertThrows(RuntimeException.class,
                () -> SqlGenerator.insertSql("t", new ArrayList<>()));
    }

    /**
     * Encoder编解码可逆。
     */
    @Test
    public void testEncoderRoundTrip() {
        String s = "Hello, 世界! 123";
        assertEquals(s, Encoder.decode(Encoder.encode(s)));
    }

    /**
     * MessageDigests: 同一字符串摘要确定且UTF-8固定(与平台默认charset无关)。
     */
    @Test
    public void testMessageDigestsDeterministic() {
        String s = "配置内容abc";
        assertEquals(MessageDigests.md5(s), MessageDigests.md5(s));
        // 与显式UTF-8一致
        assertEquals(MessageDigests.md5(s, "UTF-8"), MessageDigests.md5(s));
    }

    /**
     * Files.getBytes/write可正常读写(getBytes改用readAllBytes)。
     */
    @Test
    public void testFilesReadWrite() throws Exception {
        Path dir = java.nio.file.Files.createTempDirectory("files");
        File f = dir.resolve("a.txt").toFile();
        Files.write(f, "hello-cudrania", false);
        byte[] bytes = Files.getBytes(f);
        assertEquals("hello-cudrania", new String(bytes, java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * TrieTree.optimize后仍能正确find所有词(修复前只优化第一个孩子, 分支子树漏优化)。
     */
    @Test
    public void testTrieTreeOptimize() {
        TrieTree tree = new TrieTree();
        String[] words = {"apple", "application", "banana", "band", "bandana"};
        for (String w : words) tree.insert(w);
        tree.optimize();
        for (String w : words) {
            assertTrue(tree.find(w), "optimize后应仍能find: " + w);
        }
        assertFalse(tree.find("app"));
        assertFalse(tree.find("ban"));
    }

    /**
     * SqlTypeConverter: 浮点按scale转换应精确(修复前new BigDecimal(double)引入二进制误差)。
     */
    @Test
    public void testSqlTypeConverterPrecision() {
        SqlTypeConverter conv = new SqlTypeConverter();
        conv.setScale(2);
        assertEquals("0.10", conv.convert(0.1));
        assertEquals("1.30", conv.convert(1.3));
    }
}
