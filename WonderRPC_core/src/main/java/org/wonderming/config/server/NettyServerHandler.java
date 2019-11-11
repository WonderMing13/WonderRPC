package org.wonderming.config.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.wonderming.config.thread.MyThreadFactory;
import org.wonderming.entity.RpcRequest;
import org.wonderming.entity.RpcResponse;
import org.wonderming.utils.ApplicationContextUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wangdeming
 * @date 2019-09-29 15:28
 **/
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest){
        //形成Netty Nio线程池接受RPC请求，处理channel逻辑处理丢给后端io线程池来处理
        NettyServer.submit(()->{
            final RpcResponse rpcResponse = new RpcResponse();
            try {
                Object bean = ApplicationContextUtil.getBean(Class.forName(rpcRequest.getInterfaceName()));
                final Class<?> aClass = bean.getClass();
                final Method proxyMethod = aClass.getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
                final Object result = proxyMethod.invoke(bean, rpcRequest.getParam());
                rpcResponse.setResponseId(rpcRequest.getRequestId()).setResult(result);
               } catch (Exception e) {
                  rpcResponse.setError(e);
               }
            ctx.channel().writeAndFlush(rpcResponse);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        log.error("server caught exception",cause);
        ctx.close();
    }
}
