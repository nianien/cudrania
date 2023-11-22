package com.cudrania.test.jackson.node;

import com.cudrania.test.jackson.serializer.AppendNodeTypeWriter;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonAppend.Prop;

/**
 * 继承该接口, 序列化时自动追加属性
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
