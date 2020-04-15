package com.tian.springframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: tian
 * @Date: 2020/4/3 21:49
 * @Desc: 最终返回通知注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterReturning {

    String value() default "";
}
