package com.cudrania.test.jackson.node;


import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 条件与
 *
 * @author liyifei
 */
@NoArgsConstructor
public class AndNode extends TypeNode implements RuleNode {

    private List<RuleNode> nodes = new ArrayList<>();

    /**
     * 多个节点条件取且
     *
     * @param nodes
     */
    public AndNode(RuleNode... nodes) {
        this.nodes.addAll(Arrays.asList(nodes));
    }

    @Override
    public RuleNode and(RuleNode node) {
        this.nodes.add(node);
        return this;
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
        for (RuleNode node : nodes) {
            if (!node.matches()) {
                return false;
            }
        }
        return true;
    }
}
