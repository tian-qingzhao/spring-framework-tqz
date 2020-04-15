package com.tian.springframework.beans.factory;

/**
 * @Author: tian
 * @Date: 2020/4/9 18:02
 * @Desc: 相互依赖出现问题时抛出此异常
 */
public class UnsatisfiedDependencyException extends RuntimeException {

    public UnsatisfiedDependencyException(){

    }

    public UnsatisfiedDependencyException(String msg){
        super(msg);
    }
}
