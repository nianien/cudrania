package com.cudrania.core.collection.wrapper;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Delegate {

    String method() default "";

}
