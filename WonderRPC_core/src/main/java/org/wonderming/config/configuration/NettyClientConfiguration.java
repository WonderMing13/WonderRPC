package org.wonderming.config.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wonderming.config.client.NettyClient;
import org.wonderming.config.properties.NettyClientProperties;
import org.wonderming.config.properties.NettyServerProperties;
import org.wonderming.config.properties.TccProperties;
import org.wonderming.config.properties.ZookeeperProperties;
import org.wonderming.utils.ApplicationContextUtil;

/**
 * @author wangdeming
 * @date 2019-10-15 17:17
 **/
@Configuration
@EnableConfigurationProperties
public class NettyClientConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NettyClientProperties nettyClientProperties(){
        return new NettyClientProperties();
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
    @ConditionalOnProperty(prefix = "wonder.netty.client",name = {"name"})
    public NettyClient nettyClient(){
        return new NettyClient();
    }
}
