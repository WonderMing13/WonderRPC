package org.wonderming.config.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wonderming.codec.decode.WonderRpcDecoder;
import org.wonderming.codec.encode.WonderRpcEncoder;
import org.wonderming.config.MyThreadFactory;
import org.wonderming.config.NettyServerProperties;
import org.wonderming.config.ZookeeperConfiguration;

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
    private ZookeeperConfiguration zookeeperConfiguration;

    /**
     * 主从线程提升性能
     */
    public void start(NettyServerProperties nettyServerProperties){
        final MyThreadFactory threadFactory = new MyThreadFactory();
        threadFactory.getExecutor().submit(()-> {
            final EventLoopGroup group = new NioEventLoopGroup(1);
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(group)
                 .channel(NioServerSocketChannel.class)
                 .childHandler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel ch) {
                         ch.pipeline()
                                 .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                 .addLast(new StringEncoder(CharsetUtil.UTF_8))
                                 .addLast(new NettyServerHandler());
                     }
                 }).option(ChannelOption.SO_BACKLOG,128)
                   .childOption(ChannelOption.SO_KEEPALIVE,true);
                final InetSocketAddress inetSocketAddress = new InetSocketAddress(nettyServerProperties.getHost(), nettyServerProperties.getPort());
                final ChannelFuture f = b.bind(inetSocketAddress).syncUninterruptibly();
                final CuratorFramework curatorFramework = zookeeperConfiguration.create();
                curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath("/register");
                f.channel().closeFuture().syncUninterruptibly();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        });
    }
}
