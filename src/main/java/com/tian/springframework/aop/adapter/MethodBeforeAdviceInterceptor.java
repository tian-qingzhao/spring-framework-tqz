package com.tian.springframework.aop.adapter;

import com.tian.springframework.aop.ReflectiveMethodInvocation;
import com.tian.springframework.aop.advice.BeforeAdvice;
import com.tian.springframework.aop.interceptor.JoinPoint;
import com.tian.springframework.aop.interceptor.MethodInterceptor;

import java.lang.reflect.Method;

/**
 * @Author: tian
 * @Date: 2020/4/11 23:17
 * @Desc: 处理前置通知
 */
public class MethodBeforeAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor, BeforeAdvice {

    private JoinPoint joinPoint;

    public MethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public Object invoke(ReflectiveMethodInvocation mi) throws Throwable{
        this.joinPoint = mi;
        before(mi.getMethod(),mi.getArguments(),mi.getThis());
        return mi.proceed();
    }

    private void before(Method method,Object[] arguments,Object target) throws Throwable{
        super.invokeAdviceMethod(this.joinPoint,null,null);
    }
}
