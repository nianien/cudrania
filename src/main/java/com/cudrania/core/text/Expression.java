package com.cudrania.core.text;

import com.cudrania.core.exception.ExceptionChecker;
import com.cudrania.core.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;

/**
 * 变量表达式解析类,变量表达式是指由左右边界符和变量名组成的字符串<br/>
 * 如: left="${",right="}",则变量表达式可表示为:"${variable}"<br/>
 * 变量表达式支持嵌套和递归<br/>
 * <code>
 * <ol>
 * <li>
 * <pre>
 * new Expression("${","}").resolve("${0}年${1}月${2}日",2012,12,21);//2012年12月21日
 * </pre>
 * </li>
 * <li>
 * <pre>
 * Map map=new HashMap();
 * map.put("year",2012);
 * map.put("month",12);
 * map.put("day",21);
 * new Expression("${","}").resolve("${year}年${month}月${day}日",map);//2012年12月21日
 * </pre>
 * </li>
 * <li>
 * <pre>
 * Map map=new HashMap();
 * map.put("0",2012);
 * map.put("1",12);
 * map.put("2",21);
 * new Expression("${","}").resolve("${0}年${1}月${2}日",map,2013);//2013年12月21日
 * </pre>
 * </li>
 * <li>
 * <pre>
 * VariableHandler handler;
 * handler.handle("year"); // 2013
 * handler.handle("month"); // 12
 * handler.handle("day"); // 21
 * new Expression("${","}").resolve("${year}年${month}月${day}日",handler);//2013年12月21日
 * </pre>
 * </li>
 * </ol>
 * </code>
 *
 * @author skyfalling
 */
public class Expression {


    /**
     * 表达式的左边界
     */
    private final String left;
    /**
     * 表达式的右边界;
     */
    private final String right;

    /**
     * 是否保留未知变量,如果不保留,则置为null
     */
    private final boolean keepUnknownVariable;

    /**
     * 默认实例,表达式形如{expression}
     */
    public final static Expression defaultExpression = new Expression("{", "}");


    /**
     * 构造函数,默认保留未知变量
     *
     * @param border 表达式的边界符,左右边界符一致
     * @see Expression#Expression(String, String, boolean)
     */
    public Expression(String border) {
        this(border, border);
    }

    /**
     * 构造函数,默认保留未知变量<br/>
     *
     * @param left  表达式的左边界符
     * @param right 表达式的右边界符
     * @see Expression#Expression(String, String, boolean)
     */
    public Expression(String left, String right) {
        this(left, right, true);
    }

    /**
     * 构造函数
     *
     * @param left                表达式的左边界符
     * @param right               表达式的右边界符
     * @param keepUnknownVariable 是否保留未知变量,如果为false,则未知变量被置为null
     */
    public Expression(String left, String right, boolean keepUnknownVariable) {
        ExceptionChecker.throwIf(StringUtils.isAnyEmpty(left, right), "the left and right borders cannot be empty!");
        this.left = left;
        this.right = right;
        this.keepUnknownVariable = keepUnknownVariable;
    }

    /**
     * 表达式左边界符
     *
     * @return
     */
    public String leftBorder() {
        return left;
    }

    /**
     * 表达式右边界符
     *
     * @return
     */
    public String rightBorder() {
        return right;
    }

    /**
     * 是否保留未知变量
     *
     * @return
     */
    public boolean keepUnknownVariable() {
        return keepUnknownVariable;
    }

    /**
     * 根据变量构建表达式,即添加左右边界符
     *
     * @param variable
     * @return
     */
    public String buildExpression(String variable) {
        return this.left + variable + this.right;
    }

    /**
     * 代入位置变量计算表达式<br/>
     * <code>
     * <pre>
     * new Expression("${","}").resolve("${0}年${1}月${2}日",2012,12,21);//2012年12月21日
     * </pre>
     * </code>
     *
     * @param expression 变量表达式
     * @param variables  位置变量
     * @return 代入变量后的表达式
     */
    public String eval(String expression, Object... variables) {
        return eval(expression, null, variables);
    }

    /**
     * 代入命名变量计算表达式<br/>
     * <code>
     * <pre>
     * Map map=new HashMap();
     * map.put("year",2012);
     * map.put("month",12);
     * map.put("day",21);
     * new Expression("${","}").resolve("${year}年${month}月${day}日",map);//2012年12月21日
     * </pre>
     * </code>
     *
     * @param expression 含变量的表达式
     * @param variables  命名变量
     * @return 代入变量后的表达式
     */
    public String eval(String expression, Map<String, ?> variables) {
        return resolve(expression, new Stack<String>(), variables);
    }

    /**
     * 代入变量计算表达式<br/>
     * 如果命名变量与位置变量相同,则取位置变量
     * <code>
     * <pre>
     * Map map=new HashMap();
     * map.put("0",2012);
     * map.put("1",12);
     * map.put("2",21);
     * new Expression("${","}").resolve("${0}年${1}月${2}日",map,2013);//2013年12月21日
     * </pre>
     * </code>
     *
     * @param expression       变量表达式
     * @param namedVariables   命名变量
     * @param indexedVariables 位置变量
     * @return 代入变量后的表达式
     */
    public String eval(String expression, Map<String, ?> namedVariables, Object... indexedVariables) {
        Map<String, Object> variablesMap = new HashMap<String, Object>();
        if (namedVariables != null) {
            variablesMap.putAll(namedVariables);
        }
        int i = 0;
        for (Object variable : indexedVariables) {
            if (variable != null) {
                variablesMap.put(String.valueOf(i++), variable);
            }
        }
        return eval(expression, variablesMap);
    }

    /**
     * 递归代入map包含的变量表达式
     *
     * @param expression    当前表达式
     * @param variableStack 已代入变量列表
     * @param map           变量声明
     * @return
     */
    private String resolve(String expression, final Stack<String> variableStack, final Map<String, ?> map) {
        if (StringUtils.isEmpty(expression))
            return expression;
        for (String resolved = ""; !resolved.equals(expression); expression = resolved) {
            resolved = this.resolve(expression, variable -> {
                if (variableStack.contains(variable))
                    throw new IllegalArgumentException("circular variable expression: " +
                            buildExpression(variable));
                Object value = map.get(variable);
                if (value instanceof String) {
                    variableStack.add(variable);
                    return resolve(value.toString(), variableStack, map);
                }
                return value;
            });
        }
        if (!variableStack.isEmpty()) {
            variableStack.pop();
        }
        return expression;
    }


    /**
     * 利用表达式变量处理对象解析表达式中包含的变量,将形如"${n}"和"{variable}"的表达式代入VariableHandler对象的处理结果<br/>
     * <code>
     * <pre>
     * Function<String,Object> function;
     * function.apply("year"); // return 2013
     * function.apply("month"); // return 12
     * function.apply("day"); // return 21
     * new Expression("${","}").resolve("${year}年${month}月${day}日",function);//2013年12月21日
     * </pre>
     * </code>
     *
     * @param expression 变量表达式
     * @param function   变量处理对象
     * @return 代入后的表达式
     */
    private String resolve(String expression, Function<String, Object> function) {
        // 左右边界符的宽度
        int nL = left.length(), nR = right.length();
        // 存储解析后的表达式子串的栈
        Stack<String> stack = new Stack<String>();
        // 表达式子串解析的临时结果
        StringBuilder variableBuilder = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (expression.substring(i).startsWith(left)) {// 遇到左边界符,则入栈
                if (variableBuilder.length() != 0) {
                    // 非边界的表达式子串不为空
                    stack.add(variableBuilder.toString());
                }
                stack.add(left);
                variableBuilder.setLength(0);
                i += nL - 1;
            } else if (expression.substring(i).startsWith(right)) {// 遇到右边界符,则出栈
                if (!stack.isEmpty()) {
                    // 左边界出栈
                    stack.pop();
                    // 解析表达式变量
                    String variable = variableBuilder.toString();
                    variableBuilder.setLength(0);
                    Object value = function.apply(variable);
                    variableBuilder.append(value != null ? value.toString() : keepUnknownVariable ? buildExpression(variable) : null);
                    // 如果栈顶不是左边界, 则将代入后的结果与栈顶元素内容合并
                    if (!stack.isEmpty() && !stack.peek().equals(left)) {
                        variableBuilder.insert(0, stack.pop());
                    }
                } else {
                    // 没有与之匹配的左边界符, 不解析
                    variableBuilder.append(right);
                }
                i += nR - 1;
            } else {
                variableBuilder.append(ch);
            }
        }
        while (!stack.isEmpty()) {
            variableBuilder.insert(0, stack.pop());
        }
        return variableBuilder.toString();
    }

}
