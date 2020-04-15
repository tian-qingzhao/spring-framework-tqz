package com.tian.springframework.beans.factory.support;

import com.tian.springframework.annotation.Controller;
import com.tian.springframework.annotation.Lazy;
import com.tian.springframework.annotation.Service;
import com.tian.springframework.beans.factory.config.BeanDefinition;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Author: tian
 * @Date: 2020/4/8 12:50
 * @Desc:
 */
public class BeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registerBeanClasses = new ArrayList<String>();

    public BeanDefinitionReader(Class<?> applicationClass) {
        initPackage(applicationClass);
    }

    public void initPackage(Class<?> applicationClass){
        Package aPackage = applicationClass.getPackage();
        doScanner(aPackage.getName());
    }

    /**
     * 读取配置文件的信息
     * @param scanPackage
     */
    private void doScanner(String scanPackage) {
        //把要扫描的报名转换为一个文件路径
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else {
                if(!file.getName().endsWith(".class")){continue;}
                String fileName = scanPackage + "." + file.getName().replace(".class","");
                registerBeanClasses.add(fileName);
            }
        }
    }

    public Properties getConfig(){
        return this.config;
    }

    /**
     * 把配置文件中扫描到的所有的配置信息转换为BeanDefinition对象，以便于以后IOC操作方法
     * @param locations
     * @return
     */
    public List<BeanDefinition> loadBeanDefinitions(String... locations){
        List<BeanDefinition> result = new ArrayList<BeanDefinition>();
        try {
            for (String className : registerBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                //1.接口不要 2.抽象类不要 3.没有Controller注解不要 4.没有Service注解不要
                if(!beanClass.isInterface() && !(Modifier.isAbstract(beanClass.getModifiers())) &&
                        (beanClass.isAnnotationPresent(Controller.class) || beanClass.isAnnotationPresent(Service.class)) ){
                    BeanDefinition beanDefinition = doCreateBeanDefinition(toLowerFirsetCase(beanClass.getSimpleName()),beanClass.getName());
                    result.add(beanDefinition);
                    //如果这个类有接口的话，把他的接口的实现类放进来，但是最终只会保留一个
                    Class<?>[] interfaces = beanClass.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        result.add(doCreateBeanDefinition(anInterface.getName(),beanClass.getName()));
                    }
                }
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private BeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName){
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        try {
            //判断这个类是否为懒加载
            Lazy lazy = Class.forName(beanClassName).getAnnotation(Lazy.class);
            if(lazy!=null){
                boolean isLazy = lazy.value();
                beanDefinition.setLazyInit(isLazy);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return beanDefinition;
    }

    /**
     * 首字母转小写
     * @param beanName
     * @return
     */
    private String toLowerFirsetCase(String beanName){
        char[] chars = beanName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
