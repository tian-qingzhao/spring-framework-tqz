package com.tian.springframework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @Author: tian
 * @Date: 2020/4/11 14:35
 * @Desc:   JDK代理，代理的是接口，既然代理的是接口，那如果没有实现类怎么办，能不能代理。
 *          答案是可以的，Mybatis就是这样的。Mybatis使用JDK动态代理来实现Mapper接口，
 *          事先保存好Mapper接口，和接口声明的方法，返回值，参数类型，然后代理类的方法
 *          调用时候使用MapperMethod这个事先放入方法缓存里的对象来真实调用功能。
 *
 *          JDK动态代理具体实现原理：
 *          通过实现InvocationHandlet接口创建自己的调用处理器；
 *          通过为Proxy类指定ClassLoader对象和一组interface来创建动态代理；
 *          通过反射机制获取动态代理类的构造函数，其唯一参数类型就是调用处理器接口类型；
*           通过构造函数创建动态代理类实例，构造时调用处理器对象作为参数参入；
 *          JDK动态代理是面向接口的代理模式，如果被代理目标没有接口那么Spring也无能为力，
 *          Spring通过Java的反射机制生产被代理接口的新的匿名实现类，重写了其中AOP的增强方法。
 *
 *         由于java的单继承，动态生成的代理类已经继承了Proxy类的，就不能再继承其他的类，
 *         所以只能靠实现被代理类的接口的形式，故JDK的动态代理必须有接口。
 */
public class JDKDynamicAopProxy implements AopProxy, InvocationHandler {

    private AdviceSupport advised;

    public JDKDynamicAopProxy(AdviceSupport adviceSupport){
        this.advised = adviceSupport;
    }

    public Object getProxy() {
        return getProxy(this.advised.getClass().getClassLoader());
    }

    /**
     * 返回代理类
     * ClassLoader loader 类加载器，指定当前目标对象使用的类加载器,获取加载器的方法是固定的；
     * Class<?>[] interfaces 指定目标对象实现的接口的类型；
     * InvocationHandler h 指定动态处理器，执行目标对象的方法时,会触发事件处理器的方法。
     * @param classLoader
     * @return
     */
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    /**
     * @param proxy 代理对象
     * @param method 目标方法
     * @param args 目标方法参数
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass());
        ReflectiveMethodInvocation methodInvocation = new ReflectiveMethodInvocation(proxy, this.advised.getTarget(), method, args, this.advised.getTargetClass(), chain);
        return methodInvocation.proceed();
    }
}
