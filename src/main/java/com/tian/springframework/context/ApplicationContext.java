package com.tian.springframework.context;

import com.tian.springframework.annotation.Aspect;
import com.tian.springframework.annotation.Autowired;
import com.tian.springframework.annotation.Controller;
import com.tian.springframework.annotation.Service;
import com.tian.springframework.aop.AdviceSupport;
import com.tian.springframework.aop.AopProxy;
import com.tian.springframework.aop.CglibAopConfig;
import com.tian.springframework.aop.JDKDynamicAopProxy;
import com.tian.springframework.aop.config.AopConfig;
import com.tian.springframework.beans.BeanFactory;
import com.tian.springframework.beans.BeanWrapper;
import com.tian.springframework.beans.factory.NoSuchBeanDefinitionException;
import com.tian.springframework.beans.factory.UnsatisfiedDependencyException;
import com.tian.springframework.beans.factory.config.BeanDefinition;
import com.tian.springframework.beans.factory.config.BeanPostProcessor;
import com.tian.springframework.beans.factory.support.BeanDefinitionReader;
import com.tian.springframework.beans.factory.support.DefaultListableBeanFactory;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: tian
 * @Date: 2020/4/8 11:56
 * @Desc: 整个spring的上下文，从IOC到DI，如果有AOP的话，也会加上
 */
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    private BeanDefinitionReader beanDefinitionReader;

    /**
     * 单例池
     */
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();

    /**
     * IOC容器
     */
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, BeanWrapper>();

    private Class<?> applicationClass;

    public ApplicationContext() {

    }

    public ApplicationContext(Class<?> applicationClass){
        this.applicationClass = applicationClass;
        refresh();
    }

    @Override
    public void refresh() {
        try {
            //加载当前项目下所有的类，此处相当于扫包工作，把当前项目所有的JavaBean放到一个list中
            beanDefinitionReader = new BeanDefinitionReader(applicationClass);
            //加载配置文件，扫描相关的类，把它们封装成BeanDefinition
            List<BeanDefinition> beanDefinitions = beanDefinitionReader.loadBeanDefinitions();
            //注册，把配置信息放到容器里面(伪IOC容器)
            doRegisterBeanDefinition(beanDefinitions);
            //把不是懒加载的类给初始化
            doAutowired();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 依赖注入
     */
    private void doAutowired() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            //非懒加载进行注入
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }
    }

    /**
     * 初始化IOC容器
     * @param beanDefinitions
     */
    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception{
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("factoryBeanName: " + beanDefinition.getFactoryBeanName() + "已经存在容器中了");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition); //根据名字注入
            super.beanDefinitionMap.put(beanDefinition.getBeanClassName(),beanDefinition); //根据类型注入
        }
    }

    /**
     * 根据名字获取bean
     * 初始化和注入分开是为了解决循环依赖的问题
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        try {
            BeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
            if (null == beanDefinition){
                throw new NoSuchBeanDefinitionException("No qualifying bean of name '" + beanName + "' available");
            }
            //TODO 初始化之前通知一下
            Object instance = null;
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
            beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
            //1.初始化
            instance = instantiateBean(beanName,beanDefinition);
            //把这个对象封装到BeanWrapper中
            BeanWrapper beanWrapper = new BeanWrapper(instance);
            this.factoryBeanInstanceCache.put(beanName,beanWrapper);
            //TODO 初始化之后通知一下
            beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            //2.注入
            populateBean(beanName,new BeanDefinition(),beanWrapper);
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据类型获取bean
     * @param beanClass
     * @return
     */
    public Object getBean(Class<?> beanClass) {
        return getBean(beanClass.getName());
    }

    /**
     * 初始化bean
     * @param beanName
     * @param beanDefinition
     */
    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) throws Exception{
        //1.根据BeanDefinition拿到要实例的对象的类名
        String beanClassName = beanDefinition.getBeanClassName();
        //2.根据反射实例化一个对象
        Object instance = null;
        if(this.factoryBeanObjectCache.containsKey(beanClassName)){
            instance = this.factoryBeanObjectCache.get(beanClassName);
        }else if(this.factoryBeanObjectCache.containsKey(beanDefinition.getFactoryBeanName())){
            instance = this.factoryBeanObjectCache.get(beanDefinition.getFactoryBeanName());
        } else {
            Class<?> clazz = Class.forName(beanClassName);
            boolean anAbstract = Modifier.isAbstract(clazz.getModifiers());
            //抽象类不能被实例化
            if(anAbstract) { return null;}
            isPopulateByFields(clazz);
            instance = clazz.newInstance();
            //处理aop
            AdviceSupport adviceSupport = instantionAopConfig(beanDefinition);
            adviceSupport.setTargetClass(clazz);
            adviceSupport.setTarget(instance);
            if(adviceSupport.getPointCutPattern() != null && adviceSupport.matchPointCut()){
                instance = createProxy(adviceSupport).getProxy();
            }
            this.factoryBeanObjectCache.put(beanClassName, instance);
            this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
        }
        return instance;
    }

    /**
     * 创建代理类
     * @param adviceSupport
     * @return
     */
    private AopProxy createProxy(AdviceSupport adviceSupport) {
        Class[] interfaces = adviceSupport.getTargetClass().getInterfaces();
        //如果有接口，就默认的通过JDK动态代理实现，如果这个类没有接口，只能通过Cglib去实现了
        if (interfaces.length > 0 ){
            return new JDKDynamicAopProxy(adviceSupport);
        }
        return new CglibAopConfig(adviceSupport);
    }

    /**
     * 初始化aop配置
     * @param beanDefinition
     * @return
     */
    private AdviceSupport instantionAopConfig(BeanDefinition beanDefinition) {
        try {
            for (Map.Entry<String, BeanDefinition> stringBeanDefinitionEntry : beanDefinitionMap.entrySet()) {
                String beanClassName = stringBeanDefinitionEntry.getValue().getBeanClassName();
                Class<?> clazz = Class.forName(beanClassName);
                boolean isAspect = clazz.isAnnotationPresent(Aspect.class);
                if (isAspect){
                    AopConfig aopConfig = new AopConfig(clazz);
                    return new AdviceSupport(aopConfig);
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new AdviceSupport();
    }

    private void isPopulateByFields(Class<?> clazz) throws Exception{
        //如果当前类有注入其他的类，被注入的那个类没有添加Controller或者Service注解的话就报错
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(Autowired.class) || declaredField.isAnnotationPresent(Resource.class)){
                Class<?> fieldType = declaredField.getType();
                BeanDefinition beanDefinition = super.beanDefinitionMap.get(fieldType.getName());
                if (beanDefinition == null) {
                    throw new UnsatisfiedDependencyException("在 '"+ clazz+ "' 类中的属性 '" + fieldType.getName() + "' 不存在容器中");
                }
                Class<?> aClass = Class.forName(beanDefinition.getBeanClassName());
                if (!(aClass.isAnnotationPresent(Controller.class) || aClass.isAnnotationPresent(Service.class))) {
                    throw new NoSuchBeanDefinitionException("No qualifying bean of type '" + aClass + "' available");
                }
            }
        }
    }

    /**
     * 注入
     * @param beanName
     * @param beanDefinition
     * @param beanWrapper
     */
    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        //如果不是控制层的类或者service层的就直接return
        if (!(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class))){
            return;
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(Autowired.class) || declaredField.isAnnotationPresent(Resource.class)){
                Autowired autowired = declaredField.getAnnotation(Autowired.class);
                Resource resource = declaredField.getAnnotation(Resource.class);
                if(null != resource){ //根据beanName注入
                    doPopulateBeanByName(instance,declaredField,resource);
                }else if(autowired != null){ //根据类型注入
                    duPopulateBeanByType(instance,declaredField,autowired);
                }
            }
        }
    }

    private void duPopulateBeanByType(Object instance, Field declaredField, Autowired autowired) {
        boolean required = autowired.required();
        //如果是默认值,就必须要注入
        if (required){
            //强制方法私有属性
            declaredField.setAccessible(true);
            try {
                if(this.factoryBeanInstanceCache.get(declaredField.getType().getName()) != null){
                    declaredField.set(instance,this.factoryBeanInstanceCache.get(declaredField.getType().getName()).getWrappedInstance());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void doPopulateBeanByName(Object instance, Field declaredField, Resource resource) {
        String resourceBeanName = resource.name().trim();
        if ("".equals(resourceBeanName)){
            //如果类上面的Autowired注解里面没有值，就是用依赖注入的那个属性名
            resourceBeanName = declaredField.getType().getName();
        }
        //强制方法私有属性
        declaredField.setAccessible(true);
        try {
            if(this.factoryBeanInstanceCache.get(resourceBeanName) != null){
                declaredField.set(instance,this.factoryBeanInstanceCache.get(resourceBeanName).getWrappedInstance());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public String[] getBeanDefinitionNames(){
        return super.beanDefinitionMap.keySet().toArray(new String[super.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return super.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.beanDefinitionReader.getConfig();
    }
}
