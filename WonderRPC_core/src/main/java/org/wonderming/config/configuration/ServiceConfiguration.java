package org.wonderming.config.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.wonderming.config.properties.NettyClientProperties;
import org.wonderming.config.properties.NettyServerProperties;
import org.wonderming.config.properties.ZookeeperProperties;
import org.wonderming.entity.RpcRequest;
import org.wonderming.strategy.IRouteStrategy;
import org.wonderming.strategy.RouteEngine;
import org.wonderming.strategy.strategyimpl.RandomRouteStrategyImpl;
import org.wonderming.utils.ApplicationContextUtil;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
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
public class ServiceConfiguration {

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    /**
     * Zookeeper保存服务消息的父节点
     */
    private static final String ZK_BASE_PATH = "/wonderRPC";

    /**
     * Curator Apache操作Zookeeper工具类
     */
    private static CuratorFramework curatorFramework;


    /**
     * 连接Zookeeper
     */
    @PostConstruct
    public void createZookeeper(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zookeeperProperties.getBaseSleepTimeMs(),zookeeperProperties.getMaxRetries());
        //单个实例创建连接管理集群
        curatorFramework = CuratorFrameworkFactory.newClient(zookeeperProperties.getAddress(), retryPolicy);
        if (curatorFramework.getState() == CuratorFrameworkState.LATENT){
            log.info("ready to connect to Zookeeper...");
            curatorFramework.start();
        }
        if (curatorFramework.getState() == CuratorFrameworkState.STARTED){
            log.info("success connect to zookeeper...");
        }
    }

    /**
     * 关闭Zookeeper
     */
    public void closeZookeeper(){
        if (curatorFramework != null){
            //Zookeeper是否连接
            if (curatorFramework.getState() == CuratorFrameworkState.STARTED){
                curatorFramework.close();
            }
        }
    }

    /**
     * 获取服务的路径
     * @param interfaceName 接口名称
     * @return 服务路径
     */
    private static String getServicePath(String interfaceName){
        return ZK_BASE_PATH + "/" + interfaceName;
    }


    private String getInterfaceName(String ifn){
        return ifn.contains("$$") ? ifn.split("\\$")[0] : ifn;
    }

    /**
     * 注册服务
     */
    public void registerService(NettyServerProperties nettyServerProperties){
        String addressStr = nettyServerProperties.getHost() + ":" + nettyServerProperties.getPort();
        //将提供者的本地服务service装载进IOC容器
        final Map<String, Object> beansWithAnnotation = ApplicationContextUtil.getBeansWithAnnotation(Service.class);
        for (Object bean: beansWithAnnotation.values()) {
            final String interfaceName = bean.getClass().getCanonicalName();
            final String interfaceStr = getServicePath(getInterfaceName(interfaceName));
            final String serverAddressStr = interfaceStr + "/" + addressStr;
            try {
                //创建wonderRPC/服务名的永久节点
                curatorFramework.create().creatingParentsIfNeeded().forPath(interfaceStr);
            } catch (Exception e) {
                if (e.getMessage().contains("NodeExist")) {
                    log.info("Path already Exist");
                }
            }
            boolean registerSuccess = false;
            while (!registerSuccess) {
                try {
                    //创建NettyServer的临时节点(根据提供者的ip:port来作为子节点,解决当多个NettyServer连接同一个Zookeeper时也能区分)
                    curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(serverAddressStr,addressStr.getBytes(StandardCharsets.UTF_8));
                    registerSuccess = true;
                } catch (Exception e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    log.info("Retry Register ZK, {}", e.getMessage());
                    try {
                        curatorFramework.delete().forPath(serverAddressStr);
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
    public String discoveryService(RpcRequest rpcRequest,NettyClientProperties nettyClientProperties) throws Exception {
        //获取路径：/wonderRPC/接口名称/NettyServer服务地址
        String servicePath =  getServicePath(rpcRequest.getInterfaceName());
        //找不到对应的服务
        if (curatorFramework.checkExists().forPath(servicePath) == null){
            throw new RuntimeException("can not find any service node on path:" + servicePath);
        }
        //获取提供者服务地址列表
        final List<String> serviceAddressList = curatorFramework.getChildren().forPath(servicePath);
        //为空的情况下 可能是服务提供者的服务接口改了名字导致请求到的地址为空 所以故作删除
        if (CollectionUtils.isEmpty(serviceAddressList)){
            curatorFramework.delete().forPath(servicePath);
            throw new RuntimeException("can not find ant address node on path:" + servicePath);
        }
        //对节点内容进行监控
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, servicePath, true);
        pathChildrenCache.start();
        pathChildrenCache.getListenable().addListener((client, event) -> {
            log.info("change event:" + event.getType().name());
            //节点内容更改的情况
            if (event.getType().name().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED.name())){
                this.discoveryService(rpcRequest,nettyClientProperties);
            }
        });
        //负载均衡服务节点
        IRouteStrategy routeStrategy = RouteEngine.queryStrategy(nettyClientProperties.getRouteStrategy());
        return new String(curatorFramework.getData().forPath(servicePath + "/" + routeStrategy.select(serviceAddressList)));
    }

    /**
     * Zookeeper的分布式锁
     */
    public static InterProcessMutex getInterProcessMutex(){
        return new InterProcessMutex(curatorFramework,"/curator/lock");
    }

}
