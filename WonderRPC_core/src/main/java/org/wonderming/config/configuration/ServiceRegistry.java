package org.wonderming.config.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.wonderming.config.properties.NettyClientProperties;
import org.wonderming.config.properties.NettyServerProperties;
import org.wonderming.config.properties.ZookeeperProperties;
import org.wonderming.entity.RpcRequest;
import org.wonderming.utils.ApplicationContextUtil;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wangdeming
 * @date 2019-09-16 15:59
 * <p>
 *     1.封装Zookeeper Client与Zookeeper Server之间的连接操作
 *     2.提供Zookeeper各种应用场景(Recipe,共享锁,集群领导选举机制)的抽象封装
 * </p>
 **/
@Slf4j
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ServiceRegistry {

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    /**
     * Zookeeper保存服务消息的父节点
     */
    private static final String ZK_BASE_PATH = "/wonderRPC";

    /**
     * Curator Apache操作Zookeeper工具类
     */
    private CuratorFramework curatorFramework;


    /**
     * 连接Zookeeper
     */
    @PostConstruct
    private void createZookeeper(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zookeeperProperties.getBaseSleepTimeMs(),zookeeperProperties.getMaxRetries());
        curatorFramework = CuratorFrameworkFactory.newClient(zookeeperProperties.getAddress() + ":" + zookeeperProperties.getPort(), retryPolicy);
        curatorFramework.start();
        log.info("成功连接zookeeper....");
    }

    /**
     * 获取服务的路径
     * @param interfaceName 接口名称
     * @return 服务路径
     */
    private static String getServicePath(String interfaceName){
        return ZK_BASE_PATH + "/" + interfaceName;
    }

    /**
     * 注册服务
     */
    public void registerService(NettyServerProperties nettyServerProperties) throws Exception {
        String serverAddressStr = nettyServerProperties.getHost() + ":" + nettyServerProperties.getPort();
        //将提供者的本地服务service装载进IOC容器
        final Map<String, Object> beansWithAnnotation = ApplicationContextUtil.getBeansWithAnnotation(Service.class);
        for (Object bean: beansWithAnnotation.values()) {
            final String interfaceName = bean.getClass().getCanonicalName();
            final String str = getServicePath(interfaceName);
            try {
                curatorFramework.create().creatingParentsIfNeeded().forPath(str);
            } catch (Exception e) {
                if (e.getMessage().contains("NodeExist")) {
                    log.info("Path already Exist");
                }
            }
            boolean registerSuccess = false;
            while (!registerSuccess) {
                try {
                    curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(str + "/" + serverAddressStr);
                    registerSuccess = true;
                } catch (Exception e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    log.info("Retry Register ZK, {}", e.getMessage());
                    try {
                        curatorFramework.delete().forPath(str + "/" + serverAddressStr);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 发现服务
     */
    public String discoveryService(RpcRequest rpcRequest) throws Exception {
        final List<String> serviceList = curatorFramework.getChildren().forPath(getServicePath(rpcRequest.getInterfaceName()));
        return serviceList.get(0);
    }

}
