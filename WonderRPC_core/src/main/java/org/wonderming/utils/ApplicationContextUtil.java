package org.wonderming.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author wangdeming
 * @date 2019-09-30 16:03
 **/
public class ApplicationContextUtil implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static ApplicationContext newApplicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
            applicationContext = ac;
            newApplicationContext = applicationContext;
    }

    //获取applicationContext
    public  static ApplicationContext getApplicationContext() {
        return newApplicationContext;
    }


    /**
     * 根据名称来获取bean对象
     * @param name bean的名称
     * @return Object
     */
    public Object getBean(String name){
        return applicationContext.getBean(name);
    }

    /**
     * 根据class类型来获取bean对象
     * @param tClass 类
     * @param <T> 类的泛型
     * @return 返回类型
     */
    public  <T> T getBean(Class<T> tClass){
        return applicationContext.getBean(tClass);
    }

    /**
     * 根据clazz类型获取spring容器中的对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public  <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    public  Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotation){
        return applicationContext.getBeansWithAnnotation(annotation);
    }
}
