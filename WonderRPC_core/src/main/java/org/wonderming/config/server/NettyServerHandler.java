package org.wonderming.config.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wonderming.config.MyThreadFactory;
import org.wonderming.entity.RpcRequest;
import org.wonderming.entity.RpcResponse;

/**
 * @author wangdeming
 * @date 2019-09-29 15:28
 **/
@Component
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Autowired
    private MyThreadFactory threadFactory;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
         threadFactory.getExecutor().submit(()-> {
               final RpcResponse rpcResponse = new RpcResponse();
               rpcResponse.setResponseId(rpcRequest.getRequestId());
         });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        log.error("server caught exception",cause);
        ctx.close();
    }
}
