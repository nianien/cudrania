package com.cudrania.spring.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Conditional} that checks for matches against the given <em>profile strings</em>.
 * <p>A profile string may contain a simple profile name (for example
 * {@code "production"}) or a profile expression. A profile expression allows
 * for more complicated profile logic to be expressed, for example
 * {@code "production & cloud"}.
 * <p>The following operators are supported in profile expressions:
 * <ul>
 * <li>{@code !} - A logical <em>not</em> of the profile</li>
 * <li>{@code &} - A logical <em>and</em> of the profiles</li>
 * <li>{@code |} - A logical <em>or</em> of the profiles</li>
 * </ul>
 * <p>Please note that the {@code &} and {@code |} operators may not be mixed
 * without using parentheses. For example {@code "a & b | c"} is not a valid
 * expression; it must be expressed as {@code "(a & b) | c"} or
 * {@code "a & (b | c)"}.
 *
 * @author scorpio
 * @version 1.0.0
 * @email tengzhe.ln@alibaba-inc.com
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnProfileCondition.class)
public @interface ConditionalOnProfile {

    String[] value();

    Operator operator() default Operator.AND;

}
