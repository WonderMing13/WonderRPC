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
public class NettyClientHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        final RpcResponse rpcResponse = JsonUtil.json2Obj(o.toString(), RpcResponse.class);
        DefaultFuture.recive(rpcResponse);
    }
}
