package com.tian.springframework.config;

import com.tian.springframework.webmvc.servlet.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

/**
 * author: tian
 * date: 2020-1-15 21:05
 * desc:
 **/
public class SpringApplication {

    public static void run(Class<?> applicationClass, String[] args) throws Exception {
        long start = System.currentTimeMillis();
        Tomcat tomcat = new Tomcat();

        PropertiesConfig propertiesConfig = new PropertiesConfig();
        tomcat.setPort(propertiesConfig.getPort());

        // 设置contextPath和路径
        Context context = tomcat.addContext(propertiesConfig.getContextPath(), null);
        //配置servlet，此处相当于初始化了整个spring上下文
        tomcat.addServlet(propertiesConfig.getContextPath(), "tianServlet", new DispatcherServlet(applicationClass));
        context.addServletMappingDecoded("/", "tianServlet");

        // 启动tomcat
        tomcat.start();
        long end = System.currentTimeMillis();
        System.out.println("启动完成,共使用了:" + (end - start) + "ms");
        // 进入监听状态,如果不进入监听状态,启动tomat后就会关闭tomcat
        tomcat.getServer().await();
    }

}
