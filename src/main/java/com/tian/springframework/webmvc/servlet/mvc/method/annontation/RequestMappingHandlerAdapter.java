package com.tian.springframework.webmvc.servlet.mvc.method.annontation;

import com.tian.springframework.annotation.RequestParam;
import com.tian.springframework.webmvc.servlet.HandlerMapping;
import com.tian.springframework.webmvc.servlet.ModelAndView;
import com.tian.springframework.webmvc.servlet.mvc.method.AbstractHandlerMethodAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: tian
 * @Date: 2020/4/10 12:56
 * @Desc:
 */
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter {

    @Override
    protected ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMapping handler) throws InvocationTargetException, IllegalAccessException {
        // 保存Controller方法里面的参数对应的位置
        Map<String,Integer> paramIndexMapping = new HashMap<String, Integer>();

        // 拿到方法里面所有带注解的参数
        Annotation[][] annotation = handler.getMethod().getParameterAnnotations();
        for(int i=0;i<annotation.length;i++){
            for(Annotation an : annotation[i]){
                if(an instanceof RequestParam){
                    String paramName = ((RequestParam) an).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName,i);
                    }
                }
            }
        }

        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if(parameterType==HttpServletRequest.class || parameterType==HttpServletResponse.class){
                paramIndexMapping.put(parameterType.getName(),i);
            }
        }

        // 从request中获取形参列表
        Map<String, String[]> parameterMap = request.getParameterMap();

        // 保存实参
        Object[] parameterValues = new Object[parameterTypes.length];

        for(Map.Entry<String,String[]> param : parameterMap.entrySet()){
            String values = Arrays.toString(parameterMap.get(param.getKey()))
                    .replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");
            if(!paramIndexMapping.containsKey(param.getKey())){continue;} // 如果这个参数不存在就跳出
            Integer index = paramIndexMapping.get(param.getKey());
            parameterValues[index] = caseStringValue(values,parameterTypes[index]);
        }

        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            Integer index = paramIndexMapping.get(HttpServletRequest.class.getName());
            parameterValues[index] = request;
        }
        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            Integer index = paramIndexMapping.get(HttpServletResponse.class.getName());
            parameterValues[index] = response;
        }

        Object result = handler.getMethod().invoke(handler.getController(), parameterValues);
        // 返回值为空
        if(result == null || result instanceof Void){
            return null;
        }
        //如果是ModelAndView返回值类型的就返回
        boolean isMv = handler.getMethod().getReturnType() == ModelAndView.class;
        if(isMv){
            return (ModelAndView)result;
        }
        return null;
    }

    /**
     * 参数类型匹配
     * @param values
     * @param parameterType
     * @return
     */
    private Object caseStringValue(String values, Class<?> parameterType) {
        if(String.class == parameterType){
            return values;
        }else if(Integer.class == parameterType){
            return Integer.valueOf(values);
        }else if(Double.class == parameterType){
            return Double.valueOf(values);
        }else {
            // 其他类型的继续判断就可以
        }
        return null;
    }
}
