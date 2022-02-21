package com.cudrania.idea.jdbc.sql;

import com.cudrania.core.functions.Param;
import com.cudrania.core.utils.StringUtils;
import com.cudrania.idea.jdbc.table.DataField;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

/**
 * 支持命名参数的SQL语句,参数形式为 :x,此外可以使用 :n(n=0,1,2...)表示位置参数<br/>
 * 当参数类型为数组和集合时,将根据元素数目扩展为: (?,?...) <br/>
 * 例如:
 * <pre>
 *     <code>SqlStatement sqlStatement1 = new SqlStatement("select * from users where
 * (userName,password) in ?",
 *     Arrays.asList(new String[]{"userName1", "password1"}, new String[]{"userName2",
 * "password2"}));
 *
 *     the code above is equivalent to:
 *
 *     SqlStatement sqlStatement2 = new SqlStatement("select * from users where (userName,password)
 * in ?",
 *     new Object[][][]{{{"userName1", "password1"}, {"userName2", "password2"}}});
 *
 *     the result of preparedSql():
 *     select * from users where (userName,password) in ((?,?),(?,?))
 *     </code>
 * </pre>
 */
public class SqlStatement {

    /**
     * Set of characters that qualify as parameter separators,
     * indicating that a parameter name in a SQL String has ended.
     */
    private static final char[] PARAMETER_SEPARATORS =
            new char[]{'"', '\'', ':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/', '\\', '<', '>', '^'};

    /**
     * Set of characters that qualify as comment or quotes starting characters.
     */
    private static final String[] START_SKIP =
            new String[]{"'", "\"", "--", "/*"};

    /**
     * Set of characters that at are the corresponding comment or quotes ending characters.
     */
    private static final String[] STOP_SKIP =
            new String[]{"'", "\"", "\n", "*/"};


    /**
     * SQL语句占位符?对应的参数值
     */
    private final List<DataField> preparedParameters = new ArrayList<DataField>();
    /**
     * 解析后含占位符的SQL语句
     */
    private final StringBuilder preparedSql = new StringBuilder();
    /**
     * 原始SQL语句
     */
    private final StringBuilder originalSql = new StringBuilder();


    /**
     * 构造方法
     */
    public SqlStatement() {
    }

    /**
     * 构造方法,传递命名参数
     *
     * @param sql
     * @param parameters
     */
    public SqlStatement(String sql, Map<String, Object> parameters) {
        this.append(sql, parameters != null ? parameters : new HashMap<String, Object>());
    }


    /**
     * 构造方法,传递位置参数
     *
     * @param sql
     * @param parameters
     */
    public SqlStatement(String sql, Object... parameters) {
        this.append(sql, parameters);
    }

    /**
     * 解析后的SQL参数列表,用于{@link java.sql.PreparedStatement}
     *
     * @return
     */
    public DataField[] preparedParameters() {
        return preparedParameters.toArray(new DataField[0]);
    }

    /**
     * 解析后的SQL参数列表,用于{@link java.sql.PreparedStatement}
     *
     * @return
     */
    public Object[] preparedRawParameters() {
        List<Object> parameters = new ArrayList<>(preparedParameters.size());
        for (DataField dataField : preparedParameters) {
            parameters.add(dataField.value);
        }
        return parameters.toArray();
    }

    /**
     * 解析后的SQL语句,可用于{@link java.sql.PreparedStatement}
     *
     * @return
     */
    public String preparedSql() {
        return preparedSql.toString().trim();
    }

    /**
     * 原始SQL语句,可能包含形如:x的参数
     *
     * @return
     */
    public String originalSql() {
        return originalSql.toString().trim();
    }

    /**
     * 代入参数后的SQL语句,采用默认类型转换
     *
     * @return
     */
    public String expandSql() {
        return expandSql(new SqlTypeConverter());
    }

    /**
     * 代入参数后的SQL语句
     *
     * @param sqlTypeConverter SQL参数类型转换器
     * @return
     */
    public String expandSql(SqlTypeConverter sqlTypeConverter) {
        String[] parameters = new String[preparedParameters.size()];
        for (int i = 0; i < parameters.length; i++) {
            Object obj = preparedParameters.get(i).value;
            String quote = obj instanceof Number ? "" : "'";
            parameters[i] = quote + sqlTypeConverter.convert(obj) + quote;
        }
        return StringUtils.fill(preparedSql(), '?', parameters).trim();
    }


    /**
     * 如果expression为true,追加SQL
     *
     * @param sql
     * @param expression 布尔表达式
     * @param parameters 位置参数
     * @return
     * @see #append(String, Object...)
     */
    public SqlStatement appendIf(String sql, boolean expression, Object... parameters) {
        if (expression) {
            append(sql, parameters);
        }
        return this;
    }

    /**
     * 如果expression为true,追加SQL
     *
     * @param sql
     * @param expression 布尔表达式
     * @param parameters 命名参数值
     * @return
     */
    public SqlStatement appendIf(String sql, boolean expression, Map<String, Object> parameters) {
        if (expression) {
            append(sql, parameters);
        }
        return this;
    }


    /**
     * 追加SQL, 根据{@link Param}对象绑定参数<br/>
     * 如果{@link Param#get()} ()}有值,则绑定{@link Param#get()}的返回值
     *
     * @param sql
     * @param param 参数对象
     * @return
     */
    public <T> SqlStatement append(String sql, Param<T> param) {
        param.get().ifPresent(t -> append(sql, t));
        return this;
    }

    /**
     * 追加SQL,使用数组对象作为SQL位置参数<br/>
     * 若SQL语句中含有形如 :n(n=0,1,2...)的参数,则取parameters[n]作为SQL参数值<br/>
     * SQL中?参数按照出现位置代入
     *
     * @param sql        含有位置参数的SQL语句,参数形式为[:n],n为parameters的索引位置
     * @param parameters sql参数值列表
     * @return
     */
    public SqlStatement append(String sql, Object... parameters) {
        Map<String, Object> paramsMap = new HashMap<>();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                paramsMap.put(String.valueOf(i), parameters[i]);
            }
        }
        return this.append(sql, paramsMap);
    }


    /**
     * 追加SQL,使用Map对象作为SQL命名参数
     * 若SQL语句中含有形如 :name的参数,则取parameters.get("name")作为SQL参数值
     *
     * @param sql        含命名参数的sql语句,参数形式为[:name],name为parameters的key
     * @param parameters sql参数值列表
     * @return
     */
    public SqlStatement append(String sql, Map<String, Object> parameters) {
        parseSql(sql, parameters, originalSql, preparedSql, preparedParameters);
        this.append("");
        return this;
    }

    /**
     * 追加SQL,使用函数function返回值作为SQL参数
     *
     * @param sql
     * @param parameter 函数入参
     * @param function  返回值作为SQL参数
     * @return
     * @see #append(String, Object...)
     */
    public <T> SqlStatement append(String sql, T parameter, Function<T, ?> function) {
        return append(sql, function.apply(parameter));
    }

    /**
     * 仅追加SQL, 不作解析
     *
     * @param sql
     * @return
     */
    public SqlStatement append(String sql) {
        this.preparedSql.append(sql).append(" ");
        this.originalSql.append(sql).append(" ");
        return this;
    }

    @Override
    public String toString() {
        return originalSql.toString();
    }


    /**
     * 解析SQL语句,将形如[:x]的参数替换为符合JDBC规则的占位符.
     *
     * @param sql
     * @param parameters
     */
    private static void parseSql(String sql, Map<String, Object> parameters, StringBuilder originalSql, StringBuilder preparedSql, List<DataField> preparedParameters) {
        //这里统计参数个数
        int totalCount = 0;
        int lastIndex = 0;
        String sqlToUse = sql;
        char[] statement = sql.toCharArray();
        int escapes = 0;
        int i = 0;
        while (i < statement.length) {
            int skipToPosition = i;
            while (i < statement.length) {
                skipToPosition = skipCommentsAndQuotes(statement, i);
                if (i == skipToPosition) {
                    break;
                } else {
                    i = skipToPosition;
                }
            }
            if (i >= statement.length) {
                break;
            }
            char c = statement[i];
            int j = i + 1;
            if (c == ':' || c == '&') {
                if (j < statement.length && statement[j] == ':' && c == ':') {
                    // Postgres-style "::" casting operator - to be skipped.
                    i = i + 2;
                    continue;
                }
                while (j < statement.length && !isParameterSeparator(statement[j])) {
                    j++;
                }
                if (j - i > 1) {
                    String paramName = sql.substring(i + 1, j);
                    Object paramValue = parameters.get(paramName);
                    totalCount++;
                    preparedSql.append(sqlToUse, lastIndex, i - escapes);
                    substituteNamedParameter(preparedSql, paramValue, preparedParameters);
                    lastIndex = j - escapes;
                }
                i = j - 1;
            } else {
                if (c == '\\') {
                    if (j < statement.length && statement[j] == ':') {
                        // this is an escaped : and should be skipped
                        sqlToUse = sqlToUse.substring(0, i - escapes) + sqlToUse.substring(i - escapes + 1);
                        escapes++;
                        i = i + 2;
                        continue;
                    }
                }
                if (c == '?') {
                    String paramName = String.valueOf(totalCount);
                    Object paramValue = parameters.get(paramName);
                    totalCount++;
                    preparedSql.append(sqlToUse, lastIndex, i - escapes);
                    substituteNamedParameter(preparedSql, paramValue, preparedParameters);
                    lastIndex = j - escapes;
                }
            }
            i++;
        }
        preparedSql.append(sqlToUse, lastIndex, sqlToUse.length());

        originalSql.append(sqlToUse);
    }


    /**
     * 将SQL参数替换为占位符,并添加对应的参数值
     *
     * @param sqlBuilder
     * @param value
     * @param preparedParameters
     */
    private static void substituteNamedParameter(StringBuilder sqlBuilder, Object value, List<DataField> preparedParameters) {
        DataField dataField = value instanceof DataField ? (DataField) value : new DataField(null, value);
        Object paramValue = dataField.value;
        if (paramValue == null) {
            sqlBuilder.append("?");
            preparedParameters.add(dataField);
        } else if (paramValue instanceof Collection) {
            substituteNamedParameter(sqlBuilder, new DataField(dataField.name, ((Collection) paramValue).toArray(new Object[0]), dataField.type), preparedParameters);
        } else if (paramValue.getClass().isArray()) {
            sqlBuilder.append("(");
            int length = Array.getLength(paramValue);
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    sqlBuilder.append(",");
                }
                substituteNamedParameter(sqlBuilder, new DataField(dataField.name, Array.get(paramValue, i), dataField.type), preparedParameters);
            }
            sqlBuilder.append(")");
        } else {
            sqlBuilder.append("?");
            preparedParameters.add(dataField);
        }
    }

    /**
     * Skip over comments and quoted names present in an SQL statement
     *
     * @param statement character array containing SQL statement
     * @param position  current position of statement
     * @return next position to process after any comments or quotes are skipped
     */
    private static int skipCommentsAndQuotes(char[] statement, int position) {
        for (int i = 0; i < START_SKIP.length; i++) {
            if (statement[position] == START_SKIP[i].charAt(0)) {
                boolean match = true;
                for (int j = 1; j < START_SKIP[i].length(); j++) {
                    if (!(statement[position + j] == START_SKIP[i].charAt(j))) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    int offset = START_SKIP[i].length();
                    for (int m = position + offset; m < statement.length; m++) {
                        if (statement[m] == STOP_SKIP[i].charAt(0)) {
                            boolean endMatch = true;
                            int endPos = m;
                            for (int n = 1; n < STOP_SKIP[i].length(); n++) {
                                if (m + n >= statement.length) {
                                    // last comment not closed properly
                                    return statement.length;
                                }
                                if (!(statement[m + n] == STOP_SKIP[i].charAt(n))) {
                                    endMatch = false;
                                    break;
                                }
                                endPos = m + n;
                            }
                            if (endMatch) {
                                // found character sequence ending comment or quote
                                return endPos + 1;
                            }
                        }
                    }
                    // character sequence ending comment or quote not found
                    return statement.length;
                }
            }
        }
        return position;
    }

    /**
     * Determine whether a parameter name ends at the current position,
     * that is, whether the given character qualifies as a separator.
     */
    private static boolean isParameterSeparator(char c) {
        if (Character.isWhitespace(c)) {
            return true;
        }
        for (char separator : PARAMETER_SEPARATORS) {
            if (c == separator) {
                return true;
            }
        }
        return false;
    }

}
