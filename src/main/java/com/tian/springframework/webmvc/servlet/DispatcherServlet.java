package com.tian.springframework.webmvc.servlet;

import com.tian.demo.controller.DemoController;
import com.tian.demo.service.DemoService;
import com.tian.springframework.annotation.Controller;
import com.tian.springframework.annotation.RequestMapping;
import com.tian.springframework.annotation.ServletLoadOnStartUp;
import com.tian.springframework.annotation.SpringBootApplication;
import com.tian.springframework.context.ApplicationContext;
import com.tian.springframework.webmvc.servlet.mvc.method.annontation.RequestMappingHandlerAdapter;
import jdk.internal.util.xml.impl.Input;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: tian
 * @Date: 2020/4/9 18:42
 * @Desc: 如果添加了自定义的注解，切注解里面的值大于1，就进行初始化方法
 */
@Slf4j
//@WebServlet(loadOnStartup = 1,urlPatterns = "/")
public class DispatcherServlet extends HttpServlet {

    private Class<?> applicationClass;

    private ApplicationContext applicationContext;
    
    private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();

    /**
     * 一个请求路径和一个参数适配器保持一一对应关系（spring源码中这里用的是一个list，泛型为HandlerAdapter）
     */
    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<HandlerMapping, HandlerAdapter>();

    private static List<ViewResovler> viewResovlers = new ArrayList<ViewResovler>();

    Properties properties = new Properties();

    public DispatcherServlet(Class<?> applicationClass) throws Exception{
        this.applicationClass = applicationClass;
        ServletLoadOnStartUp annotation = applicationClass.getAnnotation(ServletLoadOnStartUp.class);
        if (annotation != null && annotation.loadOnStartUp() > 0){
            this.init();
        }
    }

    /**
     * get请求,在这里我们也交给post去处理
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    /**
     * post请求
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            log.info("发送请求了。。。。。。。。。。。");
            this.doDispatch(req,resp);
        }catch (Exception e){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("message",e.getCause());
            map.put("stackTrace", Arrays.toString(e.getStackTrace()));
            processDispatchResult(req,resp,new ModelAndView("500",map));
        }
    }

    /**
     * 调用请求
     * @param req
     * @param resp
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //从request中根据url获取HandlerMapping
        HandlerMapping handler = getHandler(req,resp);
        if (handler == null){
            //404
            ModelAndView modelAndView = new ModelAndView("404");
            processDispatchResult(req,resp,modelAndView);
            return;
        }
        //根据handler拿到HandlerAdapter
        HandlerAdapter handlerAdapter = getHandlerAdapter(handler);
        //从handlerAdapter中拿到ModelAndView对象
        ModelAndView modelAndView = handlerAdapter.handler(req, resp, handler);

        //通过ViewResolver解析ModelAndView，得到View对象或者json字符串（把内容响应到浏览器）
        processDispatchResult(req,resp,modelAndView);
    }

    /**
     * 通过ViewResolver解析ModelAndView，得到View对象或者json字符串
     * @param req
     * @param resp
     * @param modelAndView
     */
    public static void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ModelAndView modelAndView) throws IOException{
        if(modelAndView == null){
            return;
        }
        if(viewResovlers.isEmpty()){
            return;
        }
        for (ViewResovler viewResovler : viewResovlers) {
            //解析页面
            View view = viewResovler.resolveViewName(req,resp,modelAndView.getViewName());
            //把数据响应到浏览器
            view.reader(req,resp,modelAndView.getModel());
            //这里不return的话，有几个页面，会把同一个页面的内容输出几次
            return;
        }
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if(!this.handlerAdapters.isEmpty()){
            HandlerAdapter ha = this.handlerAdapters.get(handler);
            if (ha.supports(handler)){
                return ha;
            }
        }
        return null;
    }

    private HandlerMapping getHandler(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        if(this.handlerMappings.isEmpty()){
            return null;
        }
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String url = requestURI.replace(contextPath, "").replace("/+", "/");
        for (HandlerMapping handlerMapping : handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            //匹配到了url
            if (matcher.matches()){
                return handlerMapping;
            }
        }
        return null;
    }

    /**
     * 初始化spring上下文以及mvc的三大核心组件
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        if (this.applicationClass.isAnnotationPresent(SpringBootApplication.class)){
            applicationContext = new ApplicationContext(this.applicationClass);
            initStrategies(applicationContext);
            log.info("容器初始化完成了。。。。。。");
        }else {
            throw new ServletException("没有添加启动注解。。。。。。。。。");
        }
    }

    /**
     * 初始化MVC的九大组件
     * @param context
     */
    protected void initStrategies(ApplicationContext context) throws NullPointerException{
        initMultipartResolver(context);
        initLocaleResolver(context);
        initThemeResolver(context);
        initHandlerMappings(context);
        initHandlerAdapters(context);
        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);
        initViewResolvers(context);
        initFlashMapManager(context);
    }

    /**
     * 1.多文件上传组件
     * @param context
     */
    private void initMultipartResolver(ApplicationContext context) {
    }

    /**
     * 2.本地语言环境处理
     * @param context
     */
    private void initLocaleResolver(ApplicationContext context) {
    }

    /**
     * 3.主体模板处理器
     * @param context
     */
    private void initThemeResolver(ApplicationContext context) {
    }

    /**
     * 4.保存URL和方法的映射关系
     * @param context
     */
    private void initHandlerMappings(ApplicationContext context){
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object controller = applicationContext.getBean(beanDefinitionName);
            Class<?> clazz = controller.getClass();
            if (clazz.isAnnotationPresent(Controller.class)){
                // 如果controller类里面有requestMapping注解的话
                String baseUrl = "";
                if(clazz.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                // 得到controller中所有的方法
                for(Method method : clazz.getMethods()){
                    // 如果方法上面没有加requestMapping注解的话也跳出
                    if(!method.isAnnotationPresent(RequestMapping.class)){ continue; }
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    // 此处使用一个正则，代表请求的url后面可以用前缀开始，后面匹配任意字符
                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\.",".*")).replaceAll("/+","/");
                    // 把正则编译下
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new HandlerMapping(controller,method,pattern));
                }
            }
        }
    }

    /**
     * 5.动态参数适配器
     * @param context
     */
    private void initHandlerAdapters(ApplicationContext context) {
        for (HandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping,new RequestMappingHandlerAdapter());
        }
    }

    /**
     * 6.异常拦截器
     * @param context
     */
    private void initHandlerExceptionResolvers(ApplicationContext context) {
    }

    /**
     * 7.视图提取器，从request中获取ViewName
     * @param context
     */
    private void initRequestToViewNameTranslator(ApplicationContext context) {
    }

    /**
     * 8.视图转换器，模板引擎
     * @param context
     */
    private void initViewResolvers(ApplicationContext context) {
        String template = properties.getProperty("templates");
        if (null != template){ //如果配置文件里面配置模板引擎文件夹
            template = this.getClass().getClassLoader().getResource(template).getFile();
        }else {  //没有配置就是用默认的templates
            template = this.getClass().getClassLoader().getResource("templates").getFile();
        }
        File fileDir = new File(template);
        for (File templateRootDir : fileDir.listFiles()){
            //为了一个页面兼容多个模板
            this.viewResovlers.add(new ViewResovler(template));
        }
    }

    /**
     * 9.参数缓存器
     * @param context
     */
    private void initFlashMapManager(ApplicationContext context) {
    }

    public void initTemplates(){
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("application.properties");
        try {
            if (null != is){
                properties.load(is);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
