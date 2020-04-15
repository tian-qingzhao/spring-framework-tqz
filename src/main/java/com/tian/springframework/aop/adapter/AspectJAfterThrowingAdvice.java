package com.tian.springframework.aop.adapter;

import com.tian.springframework.aop.ReflectiveMethodInvocation;
import com.tian.springframework.aop.advice.AfterAdvice;
import com.tian.springframework.aop.interceptor.MethodInterceptor;

import java.lang.reflect.Method;

/**
 * @Author: tian
 * @Date: 2020/4/11 23:36
 * @Desc: 在异常拦截通知中，spring借鉴了AspectJ这个框架，AspectJ又是基于Cglib封装的一个框架
 *        如果配置了异常通知，发生异常的方法如果try catch了，就不会执行异常通知，因为异常通知也是
 *        捕获异常才能通知，try catch之后没有抛出异常相当于没有异常
 */
public class AspectJAfterThrowingAdvice extends  AbstractAspectAdvice implements MethodInterceptor, AfterAdvice {
    
    private String throwName;

    public AspectJAfterThrowingAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public Object invoke(ReflectiveMethodInvocation mi) throws Throwable{
        try {
            return mi.proceed();
        }catch (Throwable e){
            super.invokeAdviceMethod(mi,null,e.getCause());
            e.printStackTrace();
            throw e;
        }
    }

    public void setThrowName(String aspectAfterThrowingName) {
        this.throwName = aspectAfterThrowingName;
    }
}
