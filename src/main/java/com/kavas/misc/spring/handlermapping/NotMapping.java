package com.kavas.misc.spring.handlermapping;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * Indicate the type or method that will not be used to RequestMapping
 *
 * @author skyfalling
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface NotMapping {
}
