package com.tian.springframework.aop.interceptor;

/**
 * @Author: tian
 * @Date: 2020/4/11 15:37
 * @Desc: AOP的扩展MethodInvocation接口，允许访问方法调用通过的代理。
 */
public interface ProxyMethodInvocation extends MethodInvocation{

    /**
     * 返回此方法通过调用的代理。
     * @return
     */
    Object getProxy();
}