package org.wonderming.config.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wonderming.config.properties.NettyServerProperties;
import org.wonderming.config.properties.ZookeeperProperties;
import org.wonderming.config.server.NettyServer;

import java.util.concurrent.ExecutionException;

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
    @ConditionalOnMissingBean
    public ServiceRegistry serviceRegistry(){
        return new ServiceRegistry();
    }

    @Bean
    @ConditionalOnProperty(prefix = "wonder.netty.server",name = {"host","port"})
    public NettyServer nettyServer(){
        final NettyServer nettyServer = new NettyServer();
        nettyServer.start();
        return nettyServer;
    }
}
