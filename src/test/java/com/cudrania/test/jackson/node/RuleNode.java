package com.cudrania.test.jackson.node;

/**
 * 条件节点定义
 *
 * @author liyifei
 */

public interface RuleNode extends JsonTypeInfoRuleNode/*, JsonAppendRuleNode*/ {

    /**
     * 条件与
     *
     * @param node
     * @return
     */
    RuleNode and(RuleNode node);

    /**
     * 条件或
     *
     * @param node
     * @return
     */
    RuleNode or(RuleNode node);

    /**
     * 条件非
     *
     * @return
     */
    RuleNode not();

    /**
     * 评估规则是否匹配
     *
     * @return
     */
    boolean matches();
}
