package com.cudrania.spring.condition;

import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 任务选择器, 根据系统属性判断任务是否需要加载<br/>
 * <li>
 * {@link #name()}或 {@link #value()}定义任务名称<br/>
 * 如果未指定名称, 则根据{@link org.springframework.stereotype.Component}或者
 * {@link org.springframework.context.annotation.Bean}获取任务名称<br/>
 * 如果上述注解也未指定,则默认取类名或方法名,并首字母小写
 * </li>
 * <li>
 * {@link #key()}用于选择任务的系统属性, 默认为task<br/>
 * 属性值支持通配符, 多个匹配模式以","分割, 反向选择使用"!"<br/>
 * 如: -Dtask="*Task,!*test*", 表示加载所有以Task结尾, 但不包含test的任务
 * </li>
 *
 * @author scorpio
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(TaskSelector.class)
@Component
public @interface Task {

    /**
     * 任务名称
     *
     * @return
     */
    @AliasFor("value")
    String name() default "";

    @AliasFor("name")
    String value() default "";

    /**
     * 任务描述
     *
     * @return
     */
    String desc() default "";

    /**
     * 分组名称,可通过该系统属性配置任务过滤表达式<br/>
     * 默认值为task, 如: -Dtask="*,!test*",
     *
     * @return
     */
    String group() default "task";

}
