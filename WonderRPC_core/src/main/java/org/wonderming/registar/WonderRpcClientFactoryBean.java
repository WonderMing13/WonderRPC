package org.wonderming.registar;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * @className: WonderRpcClientFactoryBean
 * @package: org.wonderming.registar
 * @author: wangdeming
 * @date: 2019-09-09 14:54
 **/
public class WonderRpcClientFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    private String name;

    private Class<?> type;

    private ApplicationContext applicationContext;

    private ClassLoader classLoader;

    private Object proxy;

    @Override
    public Object getObject() throws Exception {
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.name, "Name must be set");
        ProxyFactory proxyFactory = new ProxyFactory();
        proxy = proxyFactory.getProxy(classLoader);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
