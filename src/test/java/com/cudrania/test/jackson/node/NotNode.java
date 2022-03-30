package com.cudrania.test.jackson.node;


import lombok.NoArgsConstructor;

/**
 * 条件取反
 *
 * @author liyifei
 */
@NoArgsConstructor
public class NotNode extends TypeNode  implements RuleNode {

    private RuleNode node;

    /**
     * 对node取反
     *
     * @param node
     */
    public NotNode(RuleNode node) {
        this.node = node;
    }

    @Override
    public RuleNode and(RuleNode o1) {
        return new AndNode(this, o1);
    }

    @Override
    public RuleNode or(RuleNode o1) {
        return new OrNode(this, o1);
    }

    @Override
    public RuleNode not() {
        return node;
    }


    @Override
    public boolean matches() {
        return !node.matches();
    }
}
