package com.tian.springframework.context.support;

/**
 * @Author: tian
 * @Date: 2020/4/8 11:56
 * @Desc: IOC容器实现的顶层设计
 */
public abstract class AbstractApplicationContext {
    //受保护，只提供给子类重写
    public void refresh() throws Exception {}
}
