package com.tian.springframework.webmvc.servlet.mvc.method;

import com.tian.springframework.webmvc.servlet.HandlerAdapter;
import com.tian.springframework.webmvc.servlet.HandlerMapping;
import com.tian.springframework.webmvc.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author: tian
 * @Date: 2020/4/10 12:54
 * @Desc:
 */
public abstract class AbstractHandlerMethodAdapter implements HandlerAdapter {

    public boolean supports(HandlerMapping handler) {
        return (handler instanceof HandlerMapping);
    }

    public ModelAndView handler(HttpServletRequest request, HttpServletResponse response, HandlerMapping handler) throws InvocationTargetException, IllegalAccessException {
        return handleInternal(request, response, handler);
    }

    protected abstract ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMapping handler) throws InvocationTargetException, IllegalAccessException;
}
