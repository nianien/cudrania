package com.cudrania.common.exception;


import java.lang.annotation.*;

/**
 * 用来标记预定义异常的注解,预定义异常的信息可以作为提示信息返回给客户端
 *
 * @author skyfalling
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefinedException {
}
