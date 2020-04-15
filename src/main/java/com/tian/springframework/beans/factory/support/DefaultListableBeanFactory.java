package com.tian.springframework.beans.factory.support;

import com.tian.springframework.beans.factory.config.BeanDefinition;
import com.tian.springframework.context.support.AbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tom
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext {

    //存储注册信息的BeanDefinition,伪IOC容器
    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
}
