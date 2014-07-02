package com.cudrania.validation;

import com.cudrania.validation.ValidText.StringValidator;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Array;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验字符串以及元素类型为字符串的集合或数组<br/>
 * 对于非集合或数组对象,则校验{@link Object#toString()}
 *
 * @author skyfalling
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = StringValidator.class)
public @interface ValidText {

    String message() default "{com.cudrania.validation.ValidText.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 正则表达式,如果为空,则不校验
     *
     * @return
     */
    String regex() default "";

    /**
     * 最大长度限制,默认为{@link Integer#MAX_VALUE}
     *
     * @return
     */
    int max() default Integer.MAX_VALUE;

    /**
     * 最小长度限制,默认为0
     *
     * @return
     */
    int min() default 0;

    /**
     * 是否允许为空
     *
     * @return
     */
    boolean nullable() default false;


    class StringValidator implements ConstraintValidator<ValidText, Object> {

        private ValidText constraint;

        @Override
        public void initialize(ValidText constraintAnnotation) {
            this.constraint = constraintAnnotation;
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            if (value == null)
                return constraint.nullable();
            if (value instanceof Iterable) {
                Iterable it = (Iterable) value;
                for (Object o : it) {
                    if (!validate(o))
                        return false;
                }
            } else if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    if (!validate(Array.get(value, i)))
                        return false;
                }
            }
            return validate(value);
        }

        private boolean validate(Object value) {
            String str = value.toString();
            //是否满足长度限制
            if (str.length() > constraint.max() || str.length() < constraint.min()) {
                return false;
            }
            //是否满足正则式
            if (StringUtils.isNotEmpty(constraint.regex()) && !str.matches(constraint.regex())) {
                return false;
            }
            return true;
        }
    }
}
