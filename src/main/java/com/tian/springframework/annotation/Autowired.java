package com.tian.springframework.annotation;

import java.lang.annotation.*;

/**
 * author: tian
 * date: 2020-1-1 17:11
 * desc:
 **/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    boolean required() default true;
//    String value() default "";
}
