package com.tian.springframework.annotation;

import java.lang.annotation.*;

/**
 * author: tian
 * date: 2020-1-1 17:11
 * desc:
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

    String value() default "";
}
