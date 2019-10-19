package org.wonderming.config.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wonderming.config.MyThreadFactory;
import org.wonderming.entity.RpcRequest;
import org.wonderming.entity.RpcResponse;
import org.wonderming.utils.JsonUtil;

import java.io.IOException;

/**
 * @author wangdeming
 * @date 2019-09-29 15:28
 **/
@Component
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        final MyThreadFactory myThreadFactory = new MyThreadFactory();
        myThreadFactory.getExecutor().submit(()-> {
             try {
                 final RpcRequest rpcRequest = JsonUtil.json2Obj((String) o, RpcRequest.class);
                 final RpcResponse rpcResponse = new RpcResponse();
                 rpcResponse.setResponseId(rpcRequest.getRequestId()).setResult("JW");
                 channelHandlerContext.channel().writeAndFlush(JsonUtil.obj2Json(rpcResponse));
             } catch (IOException e) {
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
