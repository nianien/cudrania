package com.cudrania.test.jackson.node;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.Predicate;

/**
 * 表达式规则
 *
 * @author liyifei <liyifei@kuaishou.com>
 */
@Getter
@NoArgsConstructor
public class ExprNode extends TypeNode implements RuleNode {

    /**
     * 规则表达式
     */
    private String expression;

    private Predicate<String> predicate;

    /**
     * 表达式节点
     *
     * @param expression 规则表达式
     */
    public ExprNode(String expression) {
        this.expression = expression;
    }

    @Override
    public RuleNode and(RuleNode node) {
        return new AndNode(this, node);
    }

    @Override
    public RuleNode or(RuleNode node) {
        return new OrNode(this, node);
    }

    @Override
    public RuleNode not() {
        return new NotNode(this);
    }

    @Override
    public boolean matches() {
        return predicate.test(expression);
    }
}
