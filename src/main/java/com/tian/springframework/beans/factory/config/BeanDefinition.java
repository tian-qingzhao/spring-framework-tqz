package com.tian.springframework.beans.factory.config;

import lombok.Data;

/**
 * @Author: tian
 * @Date: 2020/4/8 12:12
 * @Desc: Bean的一些属性
 */
@Data
public class BeanDefinition {

    /**
     * bean的className（全类名）
     */
    private String beanClassName;

    /**
     * 是否懒加载,默认false
     */
    private boolean lazyInit = false;

    /**
     * bean的id
     */
    private String factoryBeanName;
}
