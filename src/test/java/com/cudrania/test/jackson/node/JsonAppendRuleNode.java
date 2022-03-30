package com.cudrania.test.jackson.node;

import com.cudrania.test.jackson.serializer.AppendNodeTypeWriter;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonAppend.Prop;

/**
 * 条件节点定义
 *
 * @author liyifei
 */
@JsonAppend(prepend = true,
        props = {
                @Prop(value = AppendNodeTypeWriter.class,
                        type = String.class,
                        name = "type")
        }
)
public interface JsonAppendRuleNode {


}
