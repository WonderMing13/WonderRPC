package org.wonderming.config.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import org.wonderming.codec.decode.WonderRpcDecoder;
import org.wonderming.codec.encode.WonderRpcEncoder;
import org.wonderming.config.MyThreadFactory;
import org.wonderming.config.NettyClientProperties;
import org.wonderming.entity.DefaultFuture;
import org.wonderming.entity.RpcRequest;
import org.wonderming.utils.JsonUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author wangdeming
 * @date 2019-09-29 15:26
 **/
public class NettyClient {

    private static Bootstrap b;

    private static ChannelFuture f;

    private static final EventLoopGroup WORK_GROUP = new NioEventLoopGroup(5);

    private void init(){
        final MyThreadFactory myThreadFactory = new MyThreadFactory();
        myThreadFactory.getExecutor().submit(()->{
            try {
                b = new Bootstrap();
                b.group(WORK_GROUP)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE,true)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline()
                                        .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                        .addLast(new StringEncoder(CharsetUtil.UTF_8))
                                        .addLast(new NettyClientHandler());
                            }
                        });
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }

    public void start(NettyClientProperties nettyClientProperties){
        final InetSocketAddress inetSocketAddress = new InetSocketAddress(nettyClientProperties.getHost(),nettyClientProperties.getPort());
        final RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setContent("FUCK");
        try {
            init();
            TimeUnit.MILLISECONDS.sleep(2000);
            f = b.connect(inetSocketAddress).sync();
            f.channel().writeAndFlush(JsonUtil.obj2Json(rpcRequest));
            final DefaultFuture defaultFuture = new DefaultFuture(rpcRequest);
            System.out.println(defaultFuture.get().getResult().toString());
        } catch (InterruptedException | JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            f.channel().close();
            WORK_GROUP.shutdownGracefully();
        }
    }
}
