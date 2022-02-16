package com.cudrania.test.jackson.node;

/**
 * 运算符枚举定义
 * @author liyifei <liyifei@kuaishou.com>
 */
public enum Operator {
    PAREN_OPEN("("),
    UNARY_MINUS("-", 1, false),
    UNARY_LOGICAL_NOT("!", 1, false),
    UNARY_BITWISE_NOT("~", 1, false),

    POWER("**", 2, false),
    MULTIPLY("*"),
    DIVIDE("/"),
    REMAINDER("%"),

    PLUS("+"),
    MINUS("+"),

    SHL("<<"),
    SHR(">>"),

    LT("<"),
    LE("<="),
    GT(">"),
    GE(">="),
    EQ("=="),
    NE("!="),

    BITWISE_AND("&"),
    BITWISE_OR("^"),
    BITWISE_XOR("|"),

    LOGICAL_AND("&&"),
    LOGICAL_OR("||"),

    ASSIGN("=", 2, false),
    COMMA(",", 2, false),
    PAREN_CLOSE(")");

    private final String expr;
    private final int argNums;
    private final boolean leftAssoc;

    Operator(String expr) {
        this(expr, 2, true);
    }

    Operator(String expr, int argNums, boolean leftAssoc) {
        this.expr = expr;
        this.argNums = argNums;
        this.leftAssoc = leftAssoc;
    }


    public boolean isUnary() {
        return argNums == 1;
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

    public static Operator of(String expr) {
        for (Operator op : values()) {
            if (op.expr.equals(expr)) {
                return op;
            }
        }
        return null;
    }


}