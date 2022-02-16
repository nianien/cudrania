package com.cudrania.test.jackson.node;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * 条件节点定义
 *
 * @author liyifei <liyifei@kuaishou.com>
 */
@JsonTypeInfo(use = Id.CUSTOM, property = "type")
@JsonTypeIdResolver(MyTypeIdResolver.class)
public interface JsonTypeInfoRuleNode {

}
