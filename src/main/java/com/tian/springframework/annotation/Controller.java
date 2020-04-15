package com.tian.springframework.annotation;

import java.lang.annotation.*;

/**
 * author: tian
 * date: 2020-1-1 17:12
 * desc:
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {

    String value() default "";
}
