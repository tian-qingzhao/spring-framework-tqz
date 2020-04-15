package com.tian.springframework.aop;

import com.tian.springframework.aop.adapter.AfterReturningAdviceInterceptor;
import com.tian.springframework.aop.adapter.AspectAfterAdvice;
import com.tian.springframework.aop.adapter.AspectJAfterThrowingAdvice;
import com.tian.springframework.aop.adapter.MethodBeforeAdviceInterceptor;
import com.tian.springframework.aop.config.AopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: tian
 * @Date: 2020/4/11 14:36
 * @Desc:
 */
public class AdviceSupport {

    /**
     * 要代理的目标类
     */
    private Class targetClass;

    /**
     * 目标对象
     */
    private Object target;

    /**
     * aop的相关配置(切面类、切面表达式、通知等)
     */
    private AopConfig aopConfig;

    /**
     * 经过正则解析后的正则表达式
     */
    private Pattern pointCutPattern;

    private Map<Method,List<Object>> methodCache;

    public AdviceSupport() {

    }

    public AdviceSupport(AopConfig aopConfig) {
        this.aopConfig = aopConfig;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        /*spring中的切入点表达式  execution (public * com.tian.service.impl..*.*(..))
          public为修饰符 ；第一个*号：表示返回类型，*号表示所有的类型；
          包名表示需要拦截的包名，后面的两个句点表示当前包和当前包的所有子包，com.tian.service.impl包、子孙包下所有类的方法。
          第二个*号：表示类名，*号表示所有的类。
          *(..):最后这个星号表示方法名，*号表示所有的方法，后面括弧里面表示方法的参数，两个句点表示任何参数。*/

        try {
            methodCache = new HashMap<Method, List<Object>>();
            if (this.aopConfig == null || this.aopConfig.getPointCut() == null){return;}
            //PonintCut  表达式解析为正则表达式
            String pointCut = this.aopConfig.getPointCut()
                    .replaceAll("\\.","\\\\.")
                    .replaceAll("\\\\.\\*",".*")
                    .replaceAll("\\(","\\\\(")
                    .replaceAll("\\)","\\\\)");
            String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
            pointCutPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                    pointCutForClassRegex.lastIndexOf(" ") + 1));

            Pattern pattern = Pattern.compile(pointCut);

            Class<?> aspectClass = Class.forName(this.aopConfig.getAspectClass());
            Map<String,Method> methodAdvices = new HashMap<String, Method>();
            for (Method method : aspectClass.getMethods()){
                methodAdvices.put(method.getName(),method);
            }

            for (Method method : this.getTargetClass().getMethods()){
                //保存方法名
                String methodString = method.toString();
                if(methodString.contains("throws")){
                    methodString = methodString.substring(0,methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pattern.matcher(methodString);
                //如果这个方法匹配上了切面表达式
                if (matcher.matches()){
                    //因为这几种通知执行的顺序是不会变的，所以这里用一个LinkedList来存储，依次是before、after、afterThrowing、afterRetuing
                    List<Object> advices = new LinkedList<Object>();
                    //前置通知
                    if (!(null == this.aopConfig.getAspectBefore() || "".equals(this.aopConfig.getAspectBefore()))){
                        advices.add(new MethodBeforeAdviceInterceptor(
                                methodAdvices.get(this.aopConfig.getAspectBefore().getName()),aspectClass.newInstance()));
                    }
                    //后置通知
                    if (!(null == this.aopConfig.getAspectAfter() || "".equals(this.aopConfig.getAspectAfter()))){
                        advices.add(new AspectAfterAdvice(
                                methodAdvices.get(this.aopConfig.getAspectAfter().getName()), aspectClass.newInstance()));
                    }
                    //异常通知
                    if (!(null == this.aopConfig.getAspectAfterThrowing() || "".equals(this.aopConfig.getAspectAfterThrowing()))){
                        AspectJAfterThrowingAdvice throwingAdvice =
                        new AspectJAfterThrowingAdvice(
                                methodAdvices.get(this.aopConfig.getAspectAfterThrowing().getName()),aspectClass.newInstance()
                        );
                        throwingAdvice.setThrowName("java.lang.Exception");
                        advices.add(throwingAdvice);
                    }
                    //最终返回通知
                    if (!(null == this.aopConfig.getAspectAfterReturning() || "".equals(this.aopConfig.getAspectAfterReturning()))){
                        advices.add(new AfterReturningAdviceInterceptor(
                                methodAdvices.get(this.aopConfig.getAspectAfterReturning().getName()), aspectClass.newInstance()));
                    }
                    methodCache.put(method,advices);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Object getTarget(){
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * 确定一个MethodInterceptor对象的列表，对于给定的方法，基于此配置。
     * @param method 代理的方法
     * @param targetClass 目标类
     * @return 返回一个方法拦截器列表
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception{
        List<Object> cached = this.methodCache.get(method);
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
            cached = this.methodCache.get(m);
            this.methodCache.put(method,cached);
        }
        return cached;
    }

    /**
     * 校验当前目标类是否符合切面表达式规则
     * @return
     */
    public boolean matchPointCut() {
        return this.pointCutPattern.matcher(this.targetClass.toString()).matches();
    }

    public Pattern getPointCutPattern() {
        return pointCutPattern;
    }

    public void matcherBeforeAdvice(){

    }
}
