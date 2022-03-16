package com.cudrania.jdbc.sql;

import com.cudrania.core.functions.Param;
import com.cudrania.core.utils.StringUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;

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
    private final List<DataField> preparedParameters = new ArrayList<>();
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
        this.append(sql, parameters != null ? parameters : new HashMap<>());
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
    public String renderSql() {
        return renderSql(new SqlTypeConverter());
    }

    /**
     * 代入参数后的SQL语句
     *
     * @param sqlTypeConverter SQL参数类型转换器
     * @return
     */
    public String renderSql(SqlTypeConverter sqlTypeConverter) {
        String[] parameters = new String[preparedParameters.size()];
        for (int i = 0; i < parameters.length; i++) {
            Object obj = preparedParameters.get(i).value;
            String quote = obj instanceof Number ? "" : "'";
            parameters[i] = quote + sqlTypeConverter.convert(obj) + quote;
        }
        return StringUtils.fill(preparedSql(), '?', parameters).trim();
    }


    /**
     * 将statement解析后的SQL语句和SQL参数追加到当前SqlStatement中
     *
     * @param statement
     * @return
     */
    public SqlStatement append(SqlStatement statement) {
        return append(statement.preparedSql(), statement.preparedParameters());
    }

    /**
     * 根据DataField列表生成匹配条件追加当前SqlStatement中
     *
     * @param conditions
     * @return
     */
    public SqlStatement append(Collection<DataField> conditions) {
        Iterator<DataField> iterator = conditions.iterator();
        while (iterator.hasNext()) {
            DataField field = iterator.next();
            String name = field.name;
            Object value = field.value;
            if (value != null) {
                if (value.getClass().isArray() || value instanceof Collection) {
                    this.append(SqlOperator.In.toSQL(name), field);
                } else {
                    this.append(SqlOperator.Equal.toSQL(name), field);
                }
            } else {
                this.append(SqlOperator.IsNull.toSQL(name));
            }
            if (iterator.hasNext()) {
                this.append(SqlOperator.And.toString());
            }
        }
        return this;
    }

    /**
     * 根据Map列表生成匹配条件追加当前SqlStatement中
     *
     * @param conditions
     * @return
     */
    public SqlStatement append(Map<String, ?> conditions) {
        List<DataField> dataFields = new ArrayList<DataField>();
        for (Entry<String, ?> entry : conditions.entrySet()) {
            dataFields.add(new DataField(entry.getKey(), entry.getValue()));
        }
        return append(dataFields);
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

    /**
     * 如果{@link Param#get()}返回结果有值,则绑定{@link Param#get()}的返回值
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
     * 追加SQL,根据Map键值设置SQL命名参数<br/>
     * SQL中含有形如<code>:name</code>的命名参数，则取Map.get("name")作为SQL参数值
     *
     * @param sql        SQL语句,支持命名参数":name"
     * @param parameters SQL参数列表, key对应命名参数,value对应参数值
     * @return
     */
    public SqlStatement append(String sql, Map<String, Object> parameters) {
        return append(sql, new Object[]{parameters});
    }

    /**
     * 追加SQL,并设置SQL参数列表<br/>
     * <ul>
     *     <li>如果参数为Map对象，SQL中含有形如<code>:name</code>的命名参数，则取Map.get("name")作为SQL参数值
     *     </li>
     *     <li>否则，若SQL语句中含有形如 :n(n=0,1,2...)的位置参数，则取parameters[n]作为SQL参数值值
     *     </li>
     *     <li>
     *      SQL中标准?参数按照位置参数:n来处理，n为?出现的顺序,第一个出现的"?"顺序为0
     *     </li>
     * </ul>
     *
     * @param sql        SQL语句,支持"?",":n",":name" 三种形式参数
     * @param parameters SQL参数值列表,数组元素可以是Map类型、集合类型或者简单类型
     * @return
     */
    public SqlStatement append(String sql, Object... parameters) {
        Map<String, Object> paramsMap = new HashMap<>();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                if (parameter instanceof Map) {
                    paramsMap.putAll((Map) parameter);
                } else {
                    paramsMap.put(String.valueOf(i), parameter);
                }
            }
        }
        this.parseSql(sql, paramsMap);
        return this.append("");
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
    private void parseSql(String sql, Map<String, Object> parameters) {
        //这里统计参数个数
        int totalCount = 0;
        int lastIndex = 0;
        String sqlToUse = sql;
        char[] statement = sql.toCharArray();
        int escapes = 0;
        int i = 0;
        while (i < statement.length) {
            int skipToPosition;
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
                    substituteNamedParameter(preparedSql, paramName, paramValue, preparedParameters);
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
                    substituteNamedParameter(preparedSql, null, paramValue, preparedParameters);
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
    private static void substituteNamedParameter(StringBuilder sqlBuilder, String name, Object value, List<DataField> preparedParameters) {
        DataField dataField = value instanceof DataField ? (DataField) value : new DataField(name, value);
        Object paramValue = dataField.value;
        if (paramValue == null) {
            sqlBuilder.append("?");
            preparedParameters.add(dataField);
        } else if (paramValue instanceof Collection) {
            substituteNamedParameter(sqlBuilder, name, new DataField(dataField.name, ((Collection) paramValue).toArray(new Object[0]), dataField.type), preparedParameters);
        } else if (paramValue.getClass().isArray()) {
            sqlBuilder.append("(");
            int length = Array.getLength(paramValue);
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    sqlBuilder.append(",");
                }
                substituteNamedParameter(sqlBuilder, name, new DataField(dataField.name, Array.get(paramValue, i), dataField.type), preparedParameters);
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
