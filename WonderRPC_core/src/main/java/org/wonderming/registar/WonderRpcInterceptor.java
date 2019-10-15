package org.wonderming.registar;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.MutablePropertyValues;
import org.wonderming.utils.ApplicationContextUtil;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author wangdeming
 * @date 2019-09-11 14:39
 **/
public class WonderRpcInterceptor implements MethodInterceptor {

    @Resource
    private MutablePropertyValues mutablePropertyValues;


    /**
     * beanDefinition.getPropertyValues() 获取初始化bean的属性值
     * @param mutablePropertyValues MutablePropertyValues
     */
    WonderRpcInterceptor(MutablePropertyValues mutablePropertyValues) {
        this.mutablePropertyValues = mutablePropertyValues;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final Method method = invocation.getMethod();
        final String proxyClass = (String) mutablePropertyValues.get("proxyClass");
        final Object bean = ApplicationContextUtil.getBean(Class.forName(proxyClass));
        final Class<?> aClass = bean.getClass();
        final Method proxyMethod = aClass.getMethod(method.getName(), method.getParameterTypes());
        return proxyMethod.invoke(bean, invocation.getArguments());
    }
}
