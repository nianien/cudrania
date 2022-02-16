package com.cudrania.test.jackson.node;

import com.cudrania.test.jackson.serializer.AppendNodeTypeWriter;
import com.cudrania.test.jackson.serializer.TypeAsFieldSerializer;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonAppend.Prop;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 条件节点定义
 *
 * @author liyifei <liyifei@kuaishou.com>
 */
//@JsonTypeInfo(use = Id.CLASS, property = "@class")
//@JsonAppend(prepend = true,
//        props = {
//                @Prop(value = AppendNodeTypeWriter.class,
//                        type = String.class,
//                        name = "type")
//        }
//)
public interface RuleNode {

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
