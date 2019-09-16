package org.wonderming.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author wangdeming
 * @date 2019-09-16 15:59
 **/
@Component
@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperConfiguration {

    @Autowired
    private ZookeeperProperties zkProperties;

    public CuratorFramework create(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zkProperties.getBaseSleepTimeMs(),zkProperties.getMaxRetries());
        final CuratorFramework client = CuratorFrameworkFactory.newClient(zkProperties.getAddress() + ":" + zkProperties.getPort(), retryPolicy);
        client.start();
        return client;
    }
}
