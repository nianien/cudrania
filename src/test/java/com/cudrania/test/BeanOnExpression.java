package com.cudrania.test;

import com.cudrania.spring.condition.ConditionalOnExpression;
import com.cudrania.spring.condition.Operator;

import org.springframework.stereotype.Component;

/**
 * @author scorpio
 * @version 1.0.0
 */
@Component
@ConditionalOnExpression(
        value = {"${exp1} && ${exp2}", "false"},
        operator = Operator.OR
)
public class BeanOnExpression {
}
