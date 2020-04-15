package com.tian.springframework.beans.factory.config;

/**
 * @Author: tian
 * @Date: 2020/4/9 17:20
 * @Desc: BeanPostProcessor是spring框架提供的一个扩展类点(不止一个)
 *        通过实现BeanPostProcessor接口，程序员就可以插手bean实例化的过程，从而减轻了beanFactory的负担。
 *        值得说明的是，这个接口可以设置多个，会形成一个列表，然后依次执行。
 *        (但是spring默认的怎么办？Set)
 *        比如AOP就是在bean实例后期间将切面逻辑织入bean实例中的
 *        AOP也正是通过BeanPostProcessor和IOC容器建立关系的。
 *        (由spring提供的默认的PostProcessor，spring提供了很多默认的PostProcessor，下面会一一介绍这些实现类的功能)
 *        可以来演示一下 BeanPostProcessor的使用方式(把动态代理和IOC、AOP结合起来使用)
 *        在演示之前先来熟悉一下这个接口，其实这个接口本身特别简单，简单到你发指，
 *        但是他的实现类特别复杂，同样复杂到发指。
 *        可以看看spring提供哪些默认的实现(前方高能)
 *        查看类的关系图可以知道spring提供了以下的默认实例，因为高能，故而我们只是解释几个常用的
 *        1.ApplicationContextAwarePorcessor (acap)
 *        acap后置处理器的作用是：当应用程序定义的Bean实现ApplicationContextAware接口是注入ApplicationContext对象
 *        当然这是他的第一个作用，他还有其他作用，这里不一一列举了，可以参考源码
 *        2.InitDestroyAnnotationBeanPostPorcessor
 *        用来处理自定义的初始化方法和销毁方法
 *        上次说过spring中提供了3种自定义初始化和销毁方法，分别是：
 *        a.通过@Bean指定init-method和destroy-method属性
 *        b.bean实现InitializingBean接口和实现DisposableBean
 *        c.@PostConstruct：  @PostDestroy
 *        为什么spring通过这三种方法都能完成对bean声明周期的回调呢？
 *        可以通过InitDestroyAnnotationBeanPostProcessor的源码来解释
 *        3.InstantiationAwareBeanPostProcessor
 *        4.CommonAnnotationBeanPostProcessor
 *        5.AutowiredAnnotationBeanPostPorcessor
 *        6.RequiredAnnotationBeanPostProcessor
 *        7.BeanValidationPostProcessor
 *        8.AbstractAutoProxyCreator
 */
public class BeanPostProcessor {

    /**
     * 在bean初始化之前执行
     * @param bean
     * @param beanName
     * @return
     */
    public Object postProcessBeforeInitialization(Object bean,String beanName){
        return bean;
    }

    /**
     * 在bean初始化之后执行
     * @param bean
     * @param beanName
     * @return
     */
    public Object postProcessAfterInitialization(Object bean,String beanName){
        return bean;
    }
}
