package com.tian.springframework.aop;

import com.tian.springframework.aop.interceptor.MethodInterceptor;

/**
 * @Author: tian
 * @Date: 2020/4/11 20:47
 * @Desc: 使用CGLib实现动态代理，完全不受代理类必须实现接口的限制，
 *        而且CGLib底层采用ASM字节码生成框架，使用字节码技术生成代理类，比使用Java反射效率要高。
 *        唯一需要注意的是，CGLib不能对声明为final的方法进行代理，因为CGLib原理是动态生成被代理类的子类。
 */
public class CglibAopConfig implements AopProxy, MethodInterceptor {

    public CglibAopConfig(AdviceSupport adviceSupport) {

    }

    public Object getProxy() {
        return null;
    }

    public Object getProxy(ClassLoader classLoader) {
        return null;
    }

    public Object invoke(ReflectiveMethodInvocation methodInvocation) {
        return null;
    }
}
