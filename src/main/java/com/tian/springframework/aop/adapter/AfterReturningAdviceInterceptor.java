package com.tian.springframework.aop.adapter;

import com.tian.springframework.aop.ReflectiveMethodInvocation;
import com.tian.springframework.aop.interceptor.JoinPoint;
import com.tian.springframework.aop.interceptor.MethodInterceptor;

import java.lang.reflect.Method;

/**
 * @Author: tian
 * @Date: 2020/4/12 19:16
 * @Desc: 正常返回通知，如果有异常则不会执行
 */
public class AfterReturningAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    private JoinPoint joinPoint;

    public AfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public Object invoke(ReflectiveMethodInvocation mi) throws Throwable {

        Object returnValue = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(returnValue,mi.getMethod(),mi.getArguments(),mi.getThis());
        return returnValue;
    }

    private void afterReturning(Object returnValue, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(joinPoint,returnValue,null);
    }
}
