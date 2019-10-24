package org.wonderming.config.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.wonderming.entity.DefaultFuture;
import org.wonderming.entity.RpcResponse;
import org.wonderming.utils.JsonUtil;

/**
 * @author wangdeming
 * @date 2019-09-29 15:28
 **/
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        DefaultFuture.recive(rpcResponse);
    }
}
