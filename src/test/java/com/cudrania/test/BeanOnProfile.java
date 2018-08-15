package com.cudrania.test;

import com.cudrania.spring.condition.ConditionalOnProfile;

import org.springframework.stereotype.Component;

/**
 * @author scorpio
 * @version 1.0.0
 */
@Component
@ConditionalOnProfile(value = {"(all|task)& !~task", "all"})
public class BeanOnProfile {
}
