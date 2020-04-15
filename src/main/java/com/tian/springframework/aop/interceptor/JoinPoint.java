package com.tian.springframework.aop.interceptor;

/**
 * @Author: tian
 * @Date: 2020/4/11 14:53
 * @Desc:
 */
public interface JoinPoint {

    /**
     * 进入链中的下一个拦截器。此方法的实现和语义取决于在实际的连接点类型上(参见子接口)。
     * @return 参见子接口的proceed定义
     * @throws Throwable 如果连接点抛出异常，则可抛出
     */
    Object proceed() throws Throwable;

    Object getThis();
}
