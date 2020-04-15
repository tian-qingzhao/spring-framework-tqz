package com.tian.springframework.aop;

/**
 * @Author: tian
 * @Date: 2020/4/11 14:33
 * @Desc: aop的顶层实现接口，有两个关键的实现类，JDKDynamicAopProxy,CglibAopProxy
 *          JDK动态代理是面向接口的，目标类必须实现一个接口。
 *          CGLib动态代理是通过字节码底层继承要代理类来实现（如果被代理类被final关键字所修饰，那么抱歉会失败）。
 */
public interface AopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
