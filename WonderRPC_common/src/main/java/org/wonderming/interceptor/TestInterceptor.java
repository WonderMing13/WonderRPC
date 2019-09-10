package org.wonderming.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @className: TestInterceptor
 * @package: org.wonderming.interceptor
 * @author: wangdeming
 * @date: 2019-09-10 16:08
 **/
public class TestInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        final Method method = methodInvocation.getMethod();
        System.out.println(method);
        final Class<? extends MethodInvocation> aClass = methodInvocation.getClass();
        System.out.println(aClass);
        Object obj = methodInvocation.proceed();
        System.out.println(obj.toString());
        return obj;
    }
}
