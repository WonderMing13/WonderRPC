package org.wonderming.registar;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @className: WonderRPCInterceptor
 * @package: org.wonderming.registar
 * @author: wangdeming
 * @date: 2019-09-11 14:39
 **/
public class WonderRPCInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return "1";
    }
}
