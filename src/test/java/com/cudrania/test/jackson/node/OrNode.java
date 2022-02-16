package com.cudrania.test.jackson.node;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 条件或
 *
 * @author liyifei <liyifei@kuaishou.com>
 */
public class OrNode extends TypeNode implements RuleNode {

    private List<RuleNode> nodes = new ArrayList<>();

    /**
     * 多个节点条件取或
     *
     * @param nodes
     */
    public OrNode(RuleNode... nodes) {
        this.nodes.addAll(Arrays.asList(nodes));
    }

    @Override
    public RuleNode or(RuleNode node) {
        this.nodes.add(node);
        return this;
    }

    @Override
    public RuleNode and(RuleNode node) {
        return new AndNode(this, node);
    }

    @Override
    public RuleNode not() {
        return new NotNode(this);
    }

    @Override
    public boolean matches() {
        for (RuleNode node : nodes) {
            if (node.matches()) {
                return true;
            }
        }
        return false;
    }
}
