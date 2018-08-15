package com.cudrania.spring.condition;

import java.util.function.BiPredicate;

public enum Operator {
    AND {
        /**
         * 逻辑与运算
         *
         * @param context
         * @param values
         * @return
         */
        public <Value, Context> boolean matches(Value[] values, Context context, BiPredicate<Value, Context> predicate) {
            boolean matched = true;
            for (Value value : values) {
                matched &= predicate.test(value, context);
                if (!matched) {
                    break;
                }
            }
            return matched;
        }
    }, OR {
        /**
         * 逻辑或运算
         *
         * @param context
         * @param values
         * @return
         */
        public <Value, Context> boolean matches(Value[] values, Context context, BiPredicate<Value, Context> predicate) {
            boolean matched = false;
            for (Value value : values) {
                matched |= predicate.test(value, context);
                if (matched) {
                    break;
                }
            }
            return matched;
        }
    };

    public abstract <Value, Context> boolean matches(Value[] values, Context context, BiPredicate<Value, Context> predicate);

}