package com.tian.springframework.annotation;

import java.lang.annotation.*;

/**
 * author: tian
 * date: 2020-1-1 17:14
 * desc:
 **/
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String value() default "";
}
