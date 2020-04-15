package com.tian.springframework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @Author: tian
 * @Date: 2020/4/10 2:16
 * @Desc:
 */
public class ViewResovler {

    private File templateRootDir;

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    public ViewResovler() {
    }

    public ViewResovler(String template) {
//        String templateRoot = this.getClass().getResource(template).getFile();
        this.templateRootDir = new File(template);
    }

    public View resolveViewName(HttpServletRequest req, HttpServletResponse resp, String viewName) throws IOException {
        if (viewName == null || "".equals(viewName.trim())){
            //没有这个文件的话就报404
            DispatcherServlet.processDispatchResult(req,resp,new ModelAndView("404"));
            return null;
        }
        //如果这个文件的后缀是.html就是用当前文件名                           如果没有后缀就是用文件名加上我们默认的 .html
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir + "/" + viewName).replaceAll("/+", "/"));
        return new View(templateFile);
    }
}
