package com.tian.demo.aspect;

import com.tian.springframework.annotation.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: tian
 * @Date: 2020/3/5 16:52
 * @Desc: AOP在spring的执行顺序
 *          1.IOC  2.AOP  3.DI  4.MVC
 *        把代理对象进行了依赖注入，原生对象被保存到了BeanWrapper中
 */
@Aspect
@Slf4j
@Service
public class LogAspect {

    @PointCut("public .* com.tian.demo.service..*ServiceImpl..*(.*)")
    public void pointCut(){

    }

    /**
     * 方法请求之前处理
     */
    @Before
    public void before(){
        log.info("前置通知。。。");
    }

    /**
     * 异常通知，以最终返回通知只能共存一个
     */
    @AfterThrowing
    public void afterThrowing(){
        log.info("异常通知。。。");
    }

    /**
     * 有没有异常都会执行
     */
    @After
    public void after(){
        log.info("后置通知。。。");
    }

    /**
     * 正常返回的通知，如果有异常则不执行
     */
    @AfterReturning
    public void afterReturning(){
        log.info("最终返回通知。。。");
    }

}
