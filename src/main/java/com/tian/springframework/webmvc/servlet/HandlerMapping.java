package com.tian.springframework.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @Author: tian
 * @Date: 2020/4/9 20:14
 * @Desc: 映射用户的URL和对应的处理类
 */
public class HandlerMapping {

    /**
     * 方法对应的实例
     */
    private Object controller;

    /**
     * 保存映射的方法
     */
    private Method method;

    /**
     * URL正则匹配
     */
    private Pattern pattern;

    public HandlerMapping() {
    }

    public HandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
