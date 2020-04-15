package com.tian.springframework.aop.adapter;

import com.tian.springframework.aop.ReflectiveMethodInvocation;
import com.tian.springframework.aop.advice.AfterAdvice;
import com.tian.springframework.aop.interceptor.JoinPoint;
import com.tian.springframework.aop.interceptor.MethodInterceptor;

import java.lang.reflect.Method;

/**
 * @Author: tian
 * @Date: 2020/4/11 23:32
 * @Desc: 处理最终返回通知
 */
public class AspectAfterAdvice extends AbstractAspectAdvice implements MethodInterceptor, AfterAdvice {

    private JoinPoint joinPoint;

    public AspectAfterAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public Object invoke(ReflectiveMethodInvocation mi)throws Throwable{
        try {
            this.after(null,mi.getMethod(),mi.getArguments(),mi.getThis());
            return mi.proceed();
        }finally {
            this.joinPoint = mi;
        }
    }

    private void after(Object returnValue, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(joinPoint,returnValue,null);
    }
}
