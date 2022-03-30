package com.cudrania.algorithm;


import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

/**
 * 计算数学表达式的工具类,支持的操作符
 * <ul>
 * <li>UNARY_MINUS</li>
 * <li>UNARY_BITWISE_NOT</li>
 * <li>PLUS</li>
 * <li>MINUS</li>
 * <li>MULTIPLY</li>
 * <li>POWER</li>
 * <li>REMAINDER</li>
 * <li>DIVIDE</li>
 * <li>BITWISE_OR</li>
 * <li>BITWISE_AND</li>
 * <li>BITWISE_XOR</li>
 * <li>SHL</li>
 * <li>SHR</li>
 * </ul>
 *
 * @author skyfalling
 */
public class Calculator {
    private static final int TOK_WORD = 2;
    private static final int TOK_OP = 4;
    private static final int TOK_OPEN = 8;
    private static final int TOK_CLOSE = 16;


    /**
     * @param expression 操作数
     * @return
     */
    public static double calculate(String expression) {
        return doParse(tokenize(expression), Double::parseDouble);
    }


    /**
     * 运算符字符
     *
     * @param c
     * @return
     */
    private static boolean isOpChar(char c) {
        return "+-*/%<>=!^&|,(){}[]".indexOf(c) != -1;
    }

    /**
     * 解析表达式
     *
     * @param input
     * @return
     */
    private static List<String> tokenize(String input) {
        int pos = 0;
        int expected = TOK_OPEN | TOK_WORD;
        List<String> tokens = new ArrayList<>();
        while (pos < input.length()) {
            String tok = "";
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }
            if (!isOpChar(c)) {
                if ((expected & TOK_WORD) == 0) {
                    throw new IllegalArgumentException("Unexpected identifier: " + (tok + c));
                }
                expected = TOK_OP | TOK_OPEN | TOK_CLOSE;
                while (!isOpChar(c) && pos < input.length()) {
                    tok = tok + input.charAt(pos);
                    pos++;
                    if (pos < input.length()) {
                        c = input.charAt(pos);
                    } else {
                        c = 0;
                    }
                }
            } else if (c == '(' || c == ')') {
                tok = tok + c;
                pos++;
                if (c == '(' && (expected & TOK_OPEN) != 0) {
                    expected = TOK_WORD | TOK_OPEN | TOK_CLOSE;
                } else if (c == ')' && (expected & TOK_CLOSE) != 0) {
                    expected = TOK_OP | TOK_CLOSE;
                } else {
                    throw new IllegalArgumentException("Parens mismatched:" + tok);
                }
            } else {
                if ((expected & TOK_OP) == 0) {
                    if (c != '-' && c != '!' && c != '~') {
                        throw new IllegalArgumentException("Missing operand:" + (tok + c));
                    }
                    tok = tok + c;
                    pos++;
                } else {
                    String lastOp = null;
                    while (isOpChar(c) && !Character.isWhitespace(c) && c != '(' && c != ')' && pos < input.length()) {
                        if (Operator.of(tok + input.charAt(pos)) != null) {
                            tok = tok + input.charAt(pos);
                            lastOp = tok;
                        } else if (lastOp == null) {
                            tok = tok + input.charAt(pos);
                        } else {
                            break;
                        }
                        pos++;
                        if (pos < input.length()) {
                            c = input.charAt(pos);
                        } else {
                            c = 0;
                        }
                    }
                    if (lastOp == null) {
                        throw new IllegalArgumentException("Bad operator:" + (tok + c));
                    }
                }
                expected = TOK_WORD | TOK_OPEN;
            }
            tokens.add(tok);
        }
        return tokens;
    }


    /**
     * 生成规则树
     *
     * @param tokens
     * @param generator 节点生成器
     * @return
     */
    private static double doParse(List<String> tokens, Function<String, Double> generator) {
        // 变量栈
        Stack<Double> es = new Stack<>();
        // 操作符栈
        Stack<Operator> os = new Stack<>();
        // 扫描结果
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            Operator right = Operator.of(token);
            //determine op is UNARY_MINUS or MINUS
            if (right == Operator.UNARY_MINUS) {
                if (i > 0) {
                    Operator last = Operator.of(tokens.get(i - 1));
                    //上一个token是操作数或者右括号
                    if (last == null || last == Operator.PAREN_CLOSE) {
                        right = Operator.MINUS;
                    }
                }
            }
            if (right != null) {
                Operator left = os.isEmpty() ? null : os.peek();
                //ordinal越小优先级越高
                while (left != null && Operator.isPrecede(right, left) < 0) {
                    es.push(doOperate(left, es));
                    os.pop();
                    left = os.isEmpty() ? null : os.peek();
                }
                //没有匹配的括号
                if (left == null && right == Operator.PAREN_CLOSE) {
                    throw new IllegalArgumentException("Unmatched parenthesis");
                }
                //匹配括号
                if (left != null && Operator.isPrecede(right, left) == 0) {
                    os.pop();
                    continue;
                }
                //其他情况，操作符入栈
                if (left == null || Operator.isPrecede(right, left) > 0) {
                    os.push(right);
                } else {
                    os.pop();
                }
            } else {
                es.push(generator.apply(token));
            }
        }
        while (!os.isEmpty()) {
            Operator op = os.pop();
            if (op == Operator.PAREN_OPEN || op == Operator.PAREN_CLOSE) {
                throw new IllegalArgumentException("unmatched parentheses!");
            }
            double e = doOperate(op, es);
            es.push(e);
        }
        if (es.isEmpty()) {
            throw new IllegalArgumentException("illegal expression!");
        } else {
            return es.pop();
        }
    }

    /**
     * 节点运算
     *
     * @param op
     * @param stack
     * @return
     */
    private static double doOperate(Operator op, Stack<Double> stack) {
        try {
            Double b = stack.pop();
            Double a = null;
            if (!op.isUnary()) {
                a = stack.pop();
            }
            switch (op) {
                case UNARY_MINUS:
                    return -b;
                case UNARY_BITWISE_NOT:
                    return ~b.intValue();
                case PLUS:
                    return a + b;
                case MINUS:
                    return a - b;
                case MULTIPLY:
                    return a * b;
                case POWER:
                    return Math.pow(a, b);
                case REMAINDER:
                    return a % b;
                case DIVIDE:
                    return a / b;
                case BITWISE_OR:
                    return a.intValue() | b.intValue();
                case BITWISE_AND:
                    return a.intValue() & b.intValue();
                case BITWISE_XOR:
                    return a.intValue() ^ b.intValue();
                case SHL:
                    return a.intValue() << b.intValue();
                case SHR:
                    return a.intValue() >> b.intValue();
                default:
                    //Unsupported Operator
                    throw new UnsupportedOperationException("Unsupported operator:" + op);
            }
        } catch (EmptyStackException e) {
            throw new IllegalArgumentException("Missing operand:" + op);
        }
    }


    /**
     * 运算符枚举定义
     *
     * @author liyifei <liyifei@kuaishou.com>
     */
    enum Operator {
        PAREN_OPEN("("),
        UNARY_MINUS("-", 1, false),
        UNARY_LOGICAL_NOT("!", 1, false),
        UNARY_BITWISE_NOT("~", 1, false),

        POWER("**", 2, false),
        MULTIPLY("*"),
        DIVIDE("/"),
        REMAINDER("%"),

        PLUS("+"),
        MINUS("-"),

        SHL("<<"),
        SHR(">>"),

        LT("<"),
        LE("<="),
        GT(">"),
        GE(">="),
        EQ("=="),
        NE("!="),

        BITWISE_AND("&"),
        BITWISE_OR("|"),
        BITWISE_XOR("^"),

        LOGICAL_AND("&&"),
        LOGICAL_OR("||"),

        ASSIGN("=", 2, false),
        COMMA(",", 2, false),
        PAREN_CLOSE(")");

        private final String expr;
        private final int argCount;
        private final boolean leftAssoc;

        Operator(String expr) {
            this(expr, 2, true);
        }

        Operator(String expr, int argCount, boolean leftAssoc) {
            this.expr = expr;
            this.argCount = argCount;
            this.leftAssoc = leftAssoc;
        }


        public boolean isUnary() {
            return argCount == 1;
        }


        /**
         * 比较运算符优先级
         *
         * @param right
         * @param left
         * @return
         */
        public static int isPrecede(Operator right, Operator left) {
            //左右括号匹配时，优先级相等，仅此一例
            if (left == PAREN_OPEN && right == PAREN_CLOSE) {
                return 0;
            }
            //优先计算最右边的"("
            if (left == PAREN_OPEN) {
                return 1;
            }
            //右括号")"优先级最低
            if (right == PAREN_CLOSE) {
                return -1;
            }
            //优先级相等判断计算方向
            if (left == right) {
                return left.leftAssoc ? -1 : 1;
            }
            return right.ordinal() < left.ordinal() ? 1 : -1;
        }

        @Override
        public String toString() {
            return expr;
        }

        /**
         * 获取对应操作符
         *
         * @param expr
         * @return
         */
        public static Operator of(String expr) {
            for (Operator op : values()) {
                if (op.expr.equals(expr)) {
                    return op;
                }
            }
            return null;
        }
    }
}
