package com.tian.springframework.aop.interceptor;

import java.lang.reflect.Method;

/**
 * @Author: tian
 * @Date: 2020/4/11 15:33
 * @Desc: 对方法的调用的描述
 */
public interface MethodInvocation extends Invocation{

    /**
     * 获取被调用的方法。
     * @return
     */
    Method getMethod();
}
