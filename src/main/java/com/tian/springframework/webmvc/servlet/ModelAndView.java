package com.tian.springframework.webmvc.servlet;

import java.util.Map;

/**
 * @Author: tian
 * @Date: 2020/4/10 2:03
 * @Desc: 使用ModelAndView类用来存储处理完后的结果数据，以及显示该数据的视图名称
 */
public class ModelAndView {

    /**
     * 页面的名字
     */
    private String viewName;

    /**
     * 参数
     */
    private Map<String,?> model;

    public ModelAndView() {
    }

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public ModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
