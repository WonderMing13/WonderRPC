package org.wonderming.config.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wonderming.config.MyThreadFactory;
import org.wonderming.entity.RpcRequest;
import org.wonderming.entity.RpcResponse;
import org.wonderming.serializer.SerializerEngine;
import org.wonderming.serializer.SerializerEnum;
import org.wonderming.utils.ApplicationContextUtil;
import org.wonderming.utils.JsonUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wangdeming
 * @date 2019-09-29 15:28
 **/
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        final MyThreadFactory myThreadFactory = new MyThreadFactory();
        myThreadFactory.getExecutor().submit(()-> {
            final RpcResponse rpcResponse = new RpcResponse();
            try {
                Object bean = ApplicationContextUtil.getBean(Class.forName(rpcRequest.getInterfaceName()));
                final Class<?> aClass = bean.getClass();
                final Method proxyMethod = aClass.getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
                final Object result = proxyMethod.invoke(bean, rpcRequest.getParam());
                rpcResponse.setResponseId(rpcRequest.getRequestId()).setResult(result);
                channelHandlerContext.channel().writeAndFlush(rpcResponse);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        log.error("server caught exception",cause);
        ctx.close();
    }
}
