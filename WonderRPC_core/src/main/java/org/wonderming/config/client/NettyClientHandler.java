package org.wonderming.config.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.wonderming.entity.DefaultFuture;
import org.wonderming.entity.RpcResponse;
import org.wonderming.utils.JsonUtil;

/**
 * @author wangdeming
 * @date 2019-09-29 15:28
 **/
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        DefaultFuture.receive(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        log.error("client caught exception",cause);
        ctx.close();
    }
}
