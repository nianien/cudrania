package com.cudrania.test;

import com.cudrania.spring.condition.ConditionalOnProperty;

import org.springframework.stereotype.Component;

/**
 * @author scorpio
 * @version 1.0.0
 */
@Component
@ConditionalOnProperty(
        name = "${exp:exp1}", value = "true"
)
public class BeanOnProperty {
}
