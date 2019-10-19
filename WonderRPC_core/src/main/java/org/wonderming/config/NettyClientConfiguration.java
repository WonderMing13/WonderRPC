package org.wonderming.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wonderming.config.client.NettyClient;

/**
 * @author wangdeming
 * @date 2019-10-15 17:17
 **/
@Configuration
@EnableConfigurationProperties(NettyClientProperties.class)
public class NettyClientConfiguration {

    @Autowired
    private NettyClientProperties nettyClientProperties;

    @Bean
    @ConditionalOnProperty(prefix = "wonder.netty.client",name = {"name","host","port"})
    public NettyClient nettyClient(NettyClientProperties nettyClientProperties){
        final NettyClient nettyClient = new NettyClient();
        nettyClient.start(nettyClientProperties);
        return nettyClient;
    }

}
