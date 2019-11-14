package org.wonderming.registar;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * <p>
 *     1.BeanFactory是IOC的基本容器,负责生产和管理bean.DefaultListableBeanFactory,XmlBeanFactory,ApplicationContext等容器都是实现了BeanFactory。
 *     2.FactoryBean是一个接口,当在IOC容器中的Bean实现了FactoryBean接口后，通过getBean(String BeanName)获取到的Bean对象并不是FactoryBean的实现类对象,而是这个实现类中的getObject()方法返回的对象。
 *     3.要想获取FactoryBean的实现类，就要getBean(&BeanName)
 * </p>
 * @author wangdeming
 * @date 2019-09-09 14:54
 **/
public class WonderRpcClientFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware, BeanClassLoaderAware {

    private String name;

    private Class<?> type;

    private String proxyClass;

    private boolean isSync;

    private int requestTimeout;

    private MethodInterceptor methodInterceptor;

    private ApplicationContext applicationContext;

    private ClassLoader classLoader;

    private Object proxy;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

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
        proxyFactory.addInterface(type);
        proxyFactory.addAdvice(methodInterceptor);
        //是否开启优化策略
        proxyFactory.setOptimize(false);
        proxy = proxyFactory.getProxy(classLoader);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader){
        this.classLoader = classLoader;
    }

    @Override
    public boolean isSingleton(){
        return false;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setIsSync(boolean sync) {
        isSync = sync;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
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

    public String getProxyClass() {
        return proxyClass;
    }

    public void setProxyClass(String proxyClass) {
        this.proxyClass = proxyClass;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }
}
