package org.wonderming.registar;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wonderming.config.client.NettyClient;
import org.wonderming.config.thread.MyThreadFactory;
import org.wonderming.entity.DefaultFuture;
import org.wonderming.entity.RpcFuture;
import org.wonderming.entity.RpcRequest;
import org.wonderming.entity.RpcResponse;
import org.wonderming.utils.ApplicationContextUtil;
import org.wonderming.utils.SnowflakeIdWorkerUtil;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * CGLIB动态代理
 * 利用ASM开源包，对代理对象类的class文件加载进来，通过修改其字节码生成子类来处理
 *
 * jdk
 * 利用拦截器(拦截器必须实现InvocationHanlder)加上反射机制生成一个实现代理接口的匿名类
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
    public Object invoke(MethodInvocation invocation) throws Exception{
        final Method method = invocation.getMethod();
        final String proxyClass = (String) mutablePropertyValues.get("proxyClass");
        final boolean isSync = (boolean) mutablePropertyValues.get("isSync");
        final int requestTimeout = (int) mutablePropertyValues.get("requestTimeout");
        final RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(SnowflakeIdWorkerUtil.getInstance().nextId()).setInterfaceName(proxyClass).setParam(invocation.getArguments()).setMethodName(method.getName()).setParameterTypes(method.getParameterTypes());
        NettyClient nettyClient = ApplicationContextUtil.getApplicationContext().getBean(NettyClient.class);
        DefaultFuture defaultFuture = nettyClient.start(rpcRequest);
        //同步调用阻塞,最多阻塞3s真男人
        if (isSync){
            return invokeSync(defaultFuture,requestTimeout);
        }else {
            //异步调用
            return invokeAsync(defaultFuture);
        }
    }


    /**
     * 异步调用
     * @param defaultFuture DefaultFuture
     * @return RpcFuture<RpcResponse>
     */
    private RpcFuture<RpcResponse> invokeAsync(DefaultFuture defaultFuture){
        return defaultFuture;
    }

    /**
     * 同步调用
     * @param defaultFuture DefaultFuture
     * @param requestTimeout 请求超时时间
     * @return Object
     */
    private Object invokeSync(DefaultFuture defaultFuture,int requestTimeout) throws Exception {
        return defaultFuture.get(requestTimeout).getResult();
    }
}
