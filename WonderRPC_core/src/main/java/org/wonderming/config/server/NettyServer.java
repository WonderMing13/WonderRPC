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
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.wonderming.codec.decode.WonderRpcDecoder;
import org.wonderming.codec.encode.WonderRpcEncoder;
import org.wonderming.config.configuration.ServiceRegistry;
import org.wonderming.config.thread.MyThreadFactory;
import org.wonderming.config.properties.NettyServerProperties;
import org.wonderming.config.properties.ZookeeperProperties;
import org.wonderming.entity.RpcRequest;
import org.wonderming.entity.RpcResponse;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wangdeming
 * @date 2019-09-29 15:26
 **/
@AutoConfigureAfter({ServiceRegistry.class})
@EnableConfigurationProperties(NettyServerProperties.class)
public class NettyServer {

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private NettyServerProperties nettyServerProperties;

    /**
     * 主从线程提升性能
     */
    public void start(){
        final MyThreadFactory threadFactory = new MyThreadFactory();
        threadFactory.getExecutor().submit(()-> {
            final EventLoopGroup workGroup = new NioEventLoopGroup(1);
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(workGroup)
                 .channel(NioServerSocketChannel.class)
                 .childHandler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel ch) {
                         ch.pipeline()
                                 .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                 .addLast(new WonderRpcDecoder(RpcRequest.class))
                                 .addLast(new NettyServerHandler())
                                 .addLast(new WonderRpcEncoder(RpcResponse.class));
                     }
                 }).option(ChannelOption.SO_BACKLOG,128)
                   .childOption(ChannelOption.SO_KEEPALIVE,true);
                final InetSocketAddress inetSocketAddress = new InetSocketAddress(nettyServerProperties.getHost(), nettyServerProperties.getPort());
                //同步启动，RPC服务器启动完毕后才执行后续代码
                final ChannelFuture f = b.bind(inetSocketAddress).sync();
                //注册服务
                serviceRegistry.registerService(nettyServerProperties);
                //释放资源
                f.channel().closeFuture().sync();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                threadFactory.getExecutor().shutdown();
                workGroup.shutdownGracefully();
            }
        });
    }





}
