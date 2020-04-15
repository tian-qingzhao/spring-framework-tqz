package com.tian.springframework.aop.config;

import com.tian.springframework.annotation.*;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @Author: tian
 * @Date: 2020/4/11 20:29
 * @Desc: aop配置类
 */
@Data
public class AopConfig {

    /**
     * 切面类
     */
    private String aspectClass;

    /**
     * 切面表达式
     */
    private String pointCut;

    /**
     * 前置通知
     */
    private Method aspectBefore;

    /**
     * 后置通知
     */
    private Method aspectAfter;
    /**
     * 异常通知
     */
    private Method aspectAfterThrowing;

    /**
     * 最终通知(与异常通知只能共存一个)
     */
    private Method aspectAfterReturning;

    public AopConfig(Class<?> aspectClass) {
        initAopConfig(aspectClass);
    }

    public AopConfig initAopConfig(Class<?> clazz){
        if(initAspectClass(clazz)){
            initPointCutAndAdvice(clazz);
        }
        return null;
    }

    /**
     * 判断当前这个类是不是切面类
     * @param aspectClass
     * @return
     */
    public boolean initAspectClass(Class<?> aspectClass){
        if (aspectClass.isAnnotationPresent(Aspect.class)){
            return true;
        }
        return false;
    }

    /**
     * 初始化切面表达式和通知以及切面类
     * @param clazz
     * @return
     */
    public String initPointCutAndAdvice(Class<?> clazz){
        Method[] declaredMethods = clazz.getDeclaredMethods();
        this.setAspectClass(clazz.getName());
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(Before.class)){
                this.setAspectBefore(declaredMethod);
            }else if (declaredMethod.isAnnotationPresent(After.class)){
                this.setAspectAfter(declaredMethod);
            }else if (declaredMethod.isAnnotationPresent(AfterThrowing.class)){
                this.setAspectAfterThrowing(declaredMethod);
            }else if (declaredMethod.isAnnotationPresent(AfterReturning.class)){
                this.setAspectAfterReturning(declaredMethod);
            }else if (declaredMethod.isAnnotationPresent(PointCut.class)){
                this.setPointCut(declaredMethod.getAnnotation(PointCut.class).value());
            }
        }
        return null;
    }

}
