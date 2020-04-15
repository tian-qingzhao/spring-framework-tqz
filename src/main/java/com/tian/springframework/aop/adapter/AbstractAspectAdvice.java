package com.tian.springframework.aop.adapter;

import com.tian.springframework.aop.interceptor.JoinPoint;

import java.lang.reflect.Method;

/**
 * @Author: tian
 * @Date: 2020/4/11 23:57
 * @Desc: 该类供其他通知拦截的类去继承，在子类的构造方法里面调用父类的构造方法
 */
public abstract class AbstractAspectAdvice {

    /**
     * 要增强的方法
     */
    private Method aspectMethod;

    /**
     * 要增强的目标对象
     */
    private Object aspectTarget;

    public AbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    protected void invokeAdviceMethod(JoinPoint joinPoint, Object returnValues, Throwable throwable)throws Throwable{
        if(this.aspectMethod == null){
            return;
        }
        Class<?>[] parameterTypes = this.aspectMethod.getParameterTypes();
        if (null == parameterTypes || parameterTypes.length == 0){
            this.aspectMethod.invoke(this.aspectTarget);
        }else {
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0;i < args.length;i ++){
                if (parameterTypes[i] == JoinPoint.class){
                    args[i] = joinPoint;
                }else if (parameterTypes[i] == Throwable.class) {
                    args[i] = Throwable.class;
                }else if(parameterTypes[i] == Object.class){
                    args[i] = returnValues;
                }
            }
            this.aspectMethod.invoke(this.aspectTarget,args);
        }

    }

}
