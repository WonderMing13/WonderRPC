package org.wonderming.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author wangdeming
 * @date 2019-09-30 16:03
 **/
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.applicationContext = applicationContext;
    }

    private static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据名称来获取bean对象
     * @param name bean的名称
     * @return Object
     */
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    /**
     * 根据class类型来获取bean对象
     * @param tClass 类
     * @param <T> 类的泛型
     * @return 返回类型
     */
    public static  <T> T getBean(Class<T> tClass){
        return getApplicationContext().getBean(tClass);
    }

    /**
     * 根据clazz类型获取spring容器中的对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static  <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return getApplicationContext().getBeansOfType(clazz);
    }
}
