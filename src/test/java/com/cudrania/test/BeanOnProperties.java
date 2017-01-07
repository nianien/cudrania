package com.cudrania.test;

import com.cudrania.spring.condition.ConditionalOnProperties;
import com.cudrania.spring.condition.ConditionalOnProperty;
import com.cudrania.spring.condition.LogicCondition.Logic;

import org.springframework.stereotype.Component;

/**
 * @author scorpio
 * @version 1.0.0
 */
@Component
@ConditionalOnProperties(
        value = {
                @ConditionalOnProperty(name = "${exp:exp1}", value = "true"),
                @ConditionalOnProperty(name = "${exp:exp2}", value = "false")
        }, logic = Logic.OR
)
public class BeanOnProperties {
}
