package com.tian.springframework.context;

/**
 * @Author: tian
 * @Date: 2020/4/8 12:23
 * @Desc: 通过解耦方法获得IOC容器的顶层设计
 *        后面将通过一个监听器去扫描所有的类，只要实现了此接口，
 *        将自动调用setApplicationContext()方法，从而将IOC容器注入到目标类中。（此处为观察者模式）
 */
public interface ApplicationContextAware {

    /**
     * 通过该方法把上下文环境注入进来
     * @param applicationContext
     */
    void setApplicationContext(ApplicationContext applicationContext);

}
