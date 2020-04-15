package com.tian.springframework.aop.interceptor;

import com.tian.springframework.aop.ReflectiveMethodInvocation;

/**
 * @Author: tian
 * @Date: 2020/4/11 15:26
 * @Desc:
 */
public interface MethodInterceptor {

    Object invoke(ReflectiveMethodInvocation methodInvocation) throws Throwable;
}
