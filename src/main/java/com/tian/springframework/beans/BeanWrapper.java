package com.tian.springframework.beans;

/**
 * @Author: tian
 * @Date: 2020/4/8 13:53
 * @Desc:
 */
public class BeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public BeanWrapper() {
    }

    public BeanWrapper(Object wrappedInstance){
        this.wrappedInstance = wrappedInstance;
        this.wrappedClass = wrappedInstance.getClass();
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    public Class<?> getWrappedClass(){
        return wrappedClass;
    }
}
