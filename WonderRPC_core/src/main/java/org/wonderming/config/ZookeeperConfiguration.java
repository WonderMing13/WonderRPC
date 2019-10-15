package org.wonderming.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author wangdeming
 * @date 2019-09-16 15:59
 **/
@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperConfiguration {

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    @Bean
    @ConditionalOnClass(ZookeeperProperties.class)
    public CuratorFramework create(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zookeeperProperties.getBaseSleepTimeMs(),zookeeperProperties.getMaxRetries());
        final CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperProperties.getAddress() + ":" + zookeeperProperties.getPort(), retryPolicy);
        client.start();
        return client;
    }
}
