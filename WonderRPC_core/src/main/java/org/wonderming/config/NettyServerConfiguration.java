package org.wonderming.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wonderming.config.server.NettyServer;

/**
 * @author wangdeming
 * @date 2019-10-13 12:14
 **/
@Configuration
@EnableConfigurationProperties(NettyServerProperties.class)
public class NettyServerConfiguration {

    @Autowired
    private NettyServerProperties nettyServerProperties;

    @Bean
    @ConditionalOnClass(NettyServerProperties.class)
    public NettyServer nettyServer(){
        final NettyServer nettyServer = new NettyServer();
        nettyServer.start(nettyServerProperties);
        return nettyServer;
    }
}
