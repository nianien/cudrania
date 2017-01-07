package com.cudrania.test;

import com.cudrania.spring.condition.ConditionalOnExpression;
import com.cudrania.spring.condition.LogicCondition.Logic;

import org.springframework.stereotype.Component;

/**
 * @author scorpio
 * @version 1.0.0
 */
@Component
@ConditionalOnExpression(
        value = {"${exp1} && ${exp2}","false"},
        logic = Logic.OR
)
public class BeanOnExpression {
}
