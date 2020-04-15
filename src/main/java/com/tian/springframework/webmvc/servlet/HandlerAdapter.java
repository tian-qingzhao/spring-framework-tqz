package com.tian.springframework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author: tian
 * @Date: 2020/4/10 1:31
 * @Desc:
 */
public interface HandlerAdapter {

    boolean supports(HandlerMapping handler);

    ModelAndView handler(HttpServletRequest request, HttpServletResponse response,HandlerMapping handler) throws InvocationTargetException, IllegalAccessException;

}
