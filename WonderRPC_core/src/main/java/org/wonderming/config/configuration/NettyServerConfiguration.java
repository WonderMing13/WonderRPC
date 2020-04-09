package org.wonderming.config.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wonderming.config.properties.NettyServerProperties;
import org.wonderming.config.properties.TccProperties;
import org.wonderming.config.properties.ZookeeperProperties;
import org.wonderming.config.server.NettyServer;
import org.wonderming.config.server.NettyServerHandler;
import org.wonderming.config.thread.MyThreadFactory;
import org.wonderming.utils.ApplicationContextUtil;


/**
 * @author wangdeming
 * @date 2019-10-13 12:14
 **/
@Configuration
@EnableConfigurationProperties
public class NettyServerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NettyServerProperties nettyServerProperties(){
        return new NettyServerProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public ZookeeperProperties zookeeperProperties(){
        return new ZookeeperProperties();
    }

    @Bean
    @ConditionalOnProperty(prefix = "wonder.tcc",name = {"type"},matchIfMissing = true)
    public TccProperties tccProperties(){
        return new TccProperties();
    }

    @Bean
    public ApplicationContextUtil applicationContextUtil(){
        return new ApplicationContextUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceConfiguration serviceConfiguration(){
        return new ServiceConfiguration();
    }

    @Bean
    @ConditionalOnProperty(prefix = "wonder.netty.server",name = {"host","port"})
    public NettyServer nettyServer(){
        final NettyServer nettyServer = new NettyServer();
        //单线程线程池使提供者注册服务,不阻塞主线程。线程处理完毕之后回归线程池处于就绪状态
        MyThreadFactory.getSingleThreadPool().submit(nettyServer::start);
        return nettyServer;
    }
}
