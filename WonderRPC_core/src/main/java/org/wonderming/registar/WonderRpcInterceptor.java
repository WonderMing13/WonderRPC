package org.wonderming.registar;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.wonderming.config.NettyClientProperties;
import org.wonderming.config.client.NettyClient;
import org.wonderming.entity.DefaultFuture;
import org.wonderming.entity.RpcRequest;
import org.wonderming.utils.ApplicationContextUtil;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
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
    public Object invoke(MethodInvocation invocation) throws Exception {
        final Method method = invocation.getMethod();
        final String proxyClass = (String) mutablePropertyValues.get("proxyClass");
        final RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInterfaceName(proxyClass).setParam(invocation.getArguments()).setMethodName(method.getName()).setParameterTypes(method.getParameterTypes());
        final NettyClient nettyClient = new NettyClient();
        final DefaultFuture defaultFuture = nettyClient.start(rpcRequest);
        return defaultFuture.get().getResult();
    }
}
