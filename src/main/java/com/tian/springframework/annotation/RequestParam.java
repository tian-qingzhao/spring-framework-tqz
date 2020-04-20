package com.tian.springframework.annotation;

import java.lang.annotation.*;

/**
 * @Author: tian
 * @Date: 2020/2/7 19:42
 * @Desc:
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    String value() default "";
}
