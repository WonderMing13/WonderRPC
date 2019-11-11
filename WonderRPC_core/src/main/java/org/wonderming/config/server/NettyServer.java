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
import org.wonderming.config.configuration.ServiceConfiguration;
import org.wonderming.config.thread.MyThreadFactory;
import org.wonderming.config.properties.NettyServerProperties;
import org.wonderming.config.properties.ZookeeperProperties;
import org.wonderming.entity.RpcRequest;
import org.wonderming.entity.RpcResponse;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wangdeming
 * @date 2019-09-29 15:26
 **/
@AutoConfigureAfter({ServiceConfiguration.class})
@EnableConfigurationProperties(NettyServerProperties.class)
public class NettyServer {

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    @Autowired
    private NettyServerProperties nettyServerProperties;

    private static ThreadPoolExecutor threadPoolExecutor;

    private static EventLoopGroup bossGroup;

    private static EventLoopGroup workerGroup;

    /**
     * 主从线程提升性能
     */
    public void start(){
        //创建NettyServerBan对象的同时,另外启动一个线程来开启Netty的线程组
        MyThreadFactory.getSingleThreadPool().submit(() -> {
            // 配置服务端NIO线程组
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                //boss线程负责TCP的三次握手协议,work线程负责消息读写 编码解码
                b.group(bossGroup, workerGroup)
                        //创建jdk channel
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                //对数据的读写逻辑操作
                                ch.pipeline()
                                        .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                        .addLast(new WonderRpcDecoder(RpcRequest.class))
                                        .addLast(new NettyServerHandler())
                                        .addLast(new WonderRpcEncoder(RpcResponse.class));
                            }
                        }).option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                final InetSocketAddress inetSocketAddress = new InetSocketAddress(nettyServerProperties.getHost(), nettyServerProperties.getPort());
                //同步启动，RPC服务器启动完毕后才执行后续代码
                final ChannelFuture f = b.bind(inetSocketAddress).sync();
                //注册服务
                serviceConfiguration.registerService(nettyServerProperties);
                //释放资源
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    static void submit(Runnable runnable){
        if (threadPoolExecutor == null){
            synchronized (NettyServer.class){
                if (threadPoolExecutor == null){
                    threadPoolExecutor = MyThreadFactory.getExecutor();
                }
            }
        }
        threadPoolExecutor.submit(runnable);
    }
}
