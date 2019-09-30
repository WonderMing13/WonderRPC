package org.wonderming.config.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wonderming.codec.decode.WonderRpcDecoder;
import org.wonderming.codec.encode.WonderRpcEncoder;
import org.wonderming.config.MyThreadFactory;
import org.wonderming.config.NettyServerProperties;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

/**
 * @author wangdeming
 * @date 2019-09-29 15:26
 **/
@Component
@Slf4j
public class NettyServer {

    @Autowired
    private MyThreadFactory threadFactory;

    /**
     * 主从线程提升性能
     */
    public void start(NettyServerProperties nettyServerProperties){
        threadFactory.getExecutor().submit((Callable<Object>)()-> {
            final EventLoopGroup group = new NioEventLoopGroup(1);
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(group)
                 .channel(NioServerSocketChannel.class)
                 .childHandler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel ch) {
                         ch.pipeline()
                                 .addLast(new WonderRpcDecoder(65536))
                                 .addLast(new WonderRpcEncoder())
                                 .addLast(new NettyServerHandler());

                     }
                 }).option(ChannelOption.SO_BACKLOG,128)
                   .childOption(ChannelOption.SO_KEEPALIVE,true);
                final InetSocketAddress inetSocketAddress = new InetSocketAddress(nettyServerProperties.getHost(), nettyServerProperties.getPort());
                final ChannelFuture f = b.bind(inetSocketAddress).syncUninterruptibly();
                f.channel().closeFuture().syncUninterruptibly();
            }catch (Exception e){

            }
            return null;
        });
    }
}
