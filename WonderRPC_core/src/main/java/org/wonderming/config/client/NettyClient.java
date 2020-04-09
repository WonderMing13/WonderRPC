package org.wonderming.config.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.wonderming.codec.decode.WonderRpcDecoder;
import org.wonderming.codec.encode.WonderRpcEncoder;
import org.wonderming.config.configuration.ServiceConfiguration;
import org.wonderming.config.properties.NettyServerProperties;
import org.wonderming.config.thread.MyThreadFactory;
import org.wonderming.config.properties.NettyClientProperties;
import org.wonderming.entity.DefaultFuture;
import org.wonderming.entity.RpcRequest;
import org.wonderming.entity.RpcResponse;
import org.wonderming.strategy.RouteEnum;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author wangdeming
 * @date 2019-09-29 15:26
 **/
@Component
@AutoConfigureAfter({ServiceConfiguration.class})
@EnableConfigurationProperties(NettyClientProperties.class)
public class NettyClient {

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    @Autowired
    private NettyClientProperties nettyClientProperties;


    private static ThreadFactory namedThreadFactory  = new ThreadFactoryBuilder().setNameFormat("Rpc-netty-client").setDaemon(false).build();

    private static EventLoopGroup workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2,namedThreadFactory);

    public static EventLoopGroup getWorkGroup() {
        return workGroup;
    }

    public DefaultFuture start(RpcRequest rpcRequest) throws Exception {
        final String discoveryService = serviceConfiguration.discoveryService(rpcRequest,nettyClientProperties);
        final String[] strSplit = discoveryService.split(":");
        final InetSocketAddress inetSocketAddress = new InetSocketAddress(strSplit[0],Integer.valueOf(strSplit[1]));
        workGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE,true)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel){
                                channel.pipeline()
                                        .addLast(new WonderRpcEncoder(RpcRequest.class))
                                        .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                        .addLast(new WonderRpcDecoder(RpcResponse.class))
                                        .addLast(new NettyClientHandler());
                            }
                        });
                ChannelFuture f = b.connect(inetSocketAddress).sync();
                f.channel().writeAndFlush(rpcRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return new DefaultFuture(rpcRequest);
    }
}
