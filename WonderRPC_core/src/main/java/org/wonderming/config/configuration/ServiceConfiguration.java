package org.wonderming.config.configuration;

import com.google.common.collect.Lists;
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
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.wonderming.config.properties.NettyClientProperties;
import org.wonderming.config.properties.NettyServerProperties;
import org.wonderming.config.properties.TccProperties;
import org.wonderming.config.properties.ZookeeperProperties;
import org.wonderming.entity.RpcRequest;
import org.wonderming.exception.OptimisticLockException;
import org.wonderming.exception.TccTransactionException;
import org.wonderming.serializer.SerializerEngine;
import org.wonderming.serializer.SerializerEnum;
import org.wonderming.strategy.IRouteStrategy;
import org.wonderming.strategy.RouteEngine;
import org.wonderming.strategy.strategyimpl.RandomRouteStrategyImpl;
import org.wonderming.tcc.entity.Transaction;
import org.wonderming.tcc.entity.TransactionXid;
import org.wonderming.utils.ApplicationContextUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wangdeming
 * @date 2019-09-16 15:59
 * <p>
 *     1.封装Zookeeper Client与Zookeeper Server之间的连接操作
 *     2.提供Zookeeper各种应用场景(Recipe,共享锁,集群领导选举机制)的抽象封装
 * </p>
 **/
@Slf4j
@EnableConfigurationProperties({ZookeeperProperties.class, TccProperties.class})
public class ServiceConfiguration {
    private static final int SUCCESS=1;
    private static final int FAIL=0;

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    @Autowired
    private TccProperties tccProperties;

    /**
     * 分布式事务的根目录
     */
    private static final String TCC_PATH = "/tcc";

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


    /**
     * 添加Spring Aop之后,根据"$$"分割字符串获取真正的接口名称
     * @param ifn interfaceName
     * @return String 分割之后的接口名
     */
    private String getInterfaceName(String ifn){
        return ifn.contains("$$") ? ifn.split("\\$")[0] : ifn;
    }

    /**
     * 注册服务
     */
    public void registerService(NettyServerProperties nettyServerProperties){
        String addressStr = nettyServerProperties.getHost() + ":" + nettyServerProperties.getPort();
        //将提供者的本地服务service装载进IOC容器
        final Map<String, Object> beansWithAnnotation = ApplicationContextUtil.getApplicationContext().getBeansWithAnnotation(Service.class);
        for (Object bean: beansWithAnnotation.values()) {
            final String interfaceName = bean.getClass().getCanonicalName();
            final String interfaceStr = getServicePath(getInterfaceName(interfaceName));
            final String serverAddressStr = interfaceStr + "/" + addressStr;
            try {
                //创建wonderRPC/服务名的永久节点
                curatorFramework.create().creatingParentsIfNeeded().forPath(interfaceStr);
            } catch (KeeperException.NodeExistsException node) {
                log.info("Rpc Path already Exist");
            } catch (Exception e) {
                e.printStackTrace();
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

    /**
     * 根据分布式XA协议唯一Xid设为值
     * @param transactionXid TransactionXid
     * @return String
     */
    private String getTccXidPath(TransactionXid transactionXid){
        return String.format("%s/%s/%s/%s",TCC_PATH,tccProperties.getType(),new String(transactionXid.getGlobalTransactionId()),new String(transactionXid.getBranchQualifier()));
    }

    /**
     * 创建事务记录,并且赋予父节点数值
     * @param transaction Transaction
     * @return int
     */
    public int doCreate(Transaction transaction){
        try {
            String path = getTccXidPath(transaction.getXid());
            curatorFramework.create()
                            .creatingParentsIfNeeded()
                            .withMode(CreateMode.PERSISTENT)
                            .forPath(path, SerializerEngine.serialize(transaction, SerializerEnum.ProtoStuffSerializer));
        } catch (KeeperException.NodeExistsException node) {
            log.info("TCC Path already Exist");
        } catch(Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
        return SUCCESS;
    }

    /**
     * 创建分支事务的commit或者error的运行异常
     */
    public int doCreateBranchError(Transaction transaction){
        String branchError = String.format("%s/%s/%s/%s",TCC_PATH,"branchError",new String(transaction.getXid().getGlobalTransactionId()),new String(transaction.getXid().getBranchQualifier()));
        try {
            curatorFramework.create()
                            .creatingParentsIfNeeded()
                            .withMode(CreateMode.PERSISTENT)
                            .forPath(branchError,SerializerEngine.serialize(transaction,SerializerEnum.ProtoStuffSerializer));
        } catch (KeeperException.NodeExistsException node) {
            log.info("TCC Path already Exist");
        } catch(Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
        return SUCCESS;
    }

    /**
     * 创建主事务的commit或者error的运行异常
     */
    public int doCreateRootError(Transaction transaction){
        String branchError = String.format("%s/%s/%s/%s",TCC_PATH,"rootError",new String(transaction.getXid().getGlobalTransactionId()),new String(transaction.getXid().getBranchQualifier()));
        try {
            curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(branchError,SerializerEngine.serialize(transaction,SerializerEnum.ProtoStuffSerializer));
        } catch (KeeperException.NodeExistsException node) {
            log.info("TCC Path already Exist");
        } catch(Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
        return SUCCESS;
    }

    /**
     * 更新事务记录
     * @param transaction Transaction
     * @return int
     */
    public int doUpdate(Transaction transaction){
        try {
            String path = getTccXidPath(transaction.getXid());
            transaction.updateLastUpdateTime();
            transaction.updateVersion();
            //通过setData的version实现乐观锁控制。事务对象的version从1开始，zkVersion从0开始。所以要 -2
            //version不一样时会报错
            curatorFramework.setData()
                            .withVersion(transaction.getVersion() - 2)
                            .forPath(path,SerializerEngine.serialize(transaction,SerializerEnum.ProtoStuffSerializer));
            return SUCCESS;
        } catch (KeeperException.BadVersionException version) {
            throw new OptimisticLockException("OptimisticLock Bad Version");
        } catch (Exception e){
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    public int doUpdateRootError(Transaction transaction){
        try {
            String path = String.format("%s/%s/%s/%s",TCC_PATH,"rootError",new String(transaction.getXid().getGlobalTransactionId()),new String(transaction.getXid().getBranchQualifier()));
            transaction.updateLastUpdateTime();
            transaction.updateVersion();
            //version不一样时会报错
            curatorFramework.setData()
                    .withVersion(transaction.getVersion() - 4)
                    .forPath(path,SerializerEngine.serialize(transaction,SerializerEnum.ProtoStuffSerializer));
            return SUCCESS;
        } catch (KeeperException.BadVersionException version) {
            throw new OptimisticLockException("OptimisticLock Bad Version");
        } catch (Exception e){
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    public int doUpdateBranchError(Transaction transaction){
        try {
            String path = String.format("%s/%s/%s/%s",TCC_PATH,"branchError",new String(transaction.getXid().getGlobalTransactionId()),new String(transaction.getXid().getBranchQualifier()));
            transaction.updateLastUpdateTime();
            transaction.updateVersion();
            //version不一样时会报错
                curatorFramework.setData()
                        .withVersion(transaction.getVersion() - 4)
                        .forPath(path,SerializerEngine.serialize(transaction,SerializerEnum.ProtoStuffSerializer));
            return SUCCESS;
        } catch (KeeperException.BadVersionException version) {
            throw new OptimisticLockException("OptimisticLock Bad Version");
        } catch (Exception e){
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 更新主分支事务记录
     * @param transaction Transaction
     * @return int
     */
    public int updateRoot(Transaction transaction){
        try {
            String path = String.format("%s/%s/%s/%s",TCC_PATH,"root",new String(transaction.getXid().getGlobalTransactionId()),new String(transaction.getXid().getBranchQualifier()));
            transaction.updateLastUpdateTime();
            transaction.updateVersion();
            //通过setData的version实现乐观锁控制。事务对象的version从1开始，zkVersion从0开始。所以要 -2
            //version不一样时会报错
            curatorFramework.setData()
                    .withVersion(transaction.getVersion() - 2)
                    .forPath(path,SerializerEngine.serialize(transaction,SerializerEnum.ProtoStuffSerializer));
            return SUCCESS;
        } catch (KeeperException.BadVersionException version) {
            throw new OptimisticLockException("OptimisticLock Bad Version");
        } catch (Exception e){
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 删除事务记录
     * @param transaction Transaction
     * @return int
     */
    public int doDelete(Transaction transaction){
        final String path = getTccXidPath(transaction.getXid());
        try {
            curatorFramework.delete().forPath(path);
            curatorFramework.delete().forPath(String.format("%s/%s/%s",TCC_PATH,"root",new String(transaction.getXid().getGlobalTransactionId())));
            return SUCCESS;
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 删除branch事务commit/cacel错误
     * @param transaction Transaction
     * @return int
     */
    public int doDeleteWithBranchError(Transaction transaction){
        final String path = String.format("%s/%s/%s/%s",TCC_PATH,"branchError",new String(transaction.getXid().getGlobalTransactionId()),new String(transaction.getXid().getBranchQualifier()));
        try {
            curatorFramework.delete().forPath(path);
            curatorFramework.delete().forPath(String.format("%s/%s/%s",TCC_PATH,"branchError",new String(transaction.getXid().getGlobalTransactionId())));
            return SUCCESS;
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    public int doDeleteWithRootError(Transaction transaction){
        final String path = String.format("%s/%s/%s/%s",TCC_PATH,"rootError",new String(transaction.getXid().getGlobalTransactionId()),new String(transaction.getXid().getBranchQualifier()));
        try {
            curatorFramework.delete().forPath(path);
            curatorFramework.delete().forPath(String.format("%s/%s/%s",TCC_PATH,"rootError",new String(transaction.getXid().getGlobalTransactionId())));
            return SUCCESS;
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 根据transaction信息 查找事务日志记录.
     * @param transaction  事务对象
     * @return transaction
     */
    public Transaction findByXid(Transaction transaction) {
        try {
            String path = getTccXidPath(transaction.getXid());
            byte[] result = curatorFramework.getData().forPath(path);
            if(result!=null){
                return SerializerEngine.deserialize(result,Transaction.class,SerializerEnum.ProtoStuffSerializer);
            }
            return null;
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 获取所有时间超过120秒的事务(根事务和分支事务)
     * @param date 当前时间之前的120秒的Date
     * @return Map<String,List<Transaction>>
     */
    public List<Transaction> doFindAllUnmodified(Date date){
        List<Transaction> rootTransactionList = Lists.newArrayList();
        final boolean existTransaction = this.doFindExistTransaction();
        if (existTransaction){
            try {
                final List<String> rootList = curatorFramework.getChildren().forPath(String.format("%s/%s", TCC_PATH, "rootError"));
                for (String str : rootList) {
                    final String rootBranch = this.findRootErrorId(str).get(0);
                    String rootBranchPath = String.format("%s/%s/%s/%s",TCC_PATH,"rootError",str, rootBranch);
                    rootTransactionList.add(this.findByPath(rootBranchPath));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
          return rootTransactionList.stream().filter(transaction -> transaction.getLastUpdateTime().getTime() < date.getTime()).collect(Collectors.toList());
        }
        return rootTransactionList;
    }

    public List<Transaction> doFindAllUnmodifiedWithBranchError(Date date){
        List<Transaction> rootTransactionList = Lists.newArrayList();
        final boolean existTransaction = this.doFindExistTransactionWithBranchError();
        if (existTransaction){
            try {
                final List<String> rootList = curatorFramework.getChildren().forPath(String.format("%s/%s", TCC_PATH, "branchError"));
                for (String str : rootList) {
                    final String rootBranch = this.findBranchErrorId(str).get(0);
                    String rootBranchPath = String.format("%s/%s/%s/%s",TCC_PATH,"branchError",str, rootBranch);
                    rootTransactionList.add(this.findByPath(rootBranchPath));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rootTransactionList.stream().filter(transaction -> transaction.getLastUpdateTime().getTime() < date.getTime()).collect(Collectors.toList());
        }
        return rootTransactionList;
    }

    /**
     * 找出根事务
     * @param globalId String
     * @return List<String>
     */
    public List<String> findRootId(String globalId){
        try {
            return curatorFramework.getChildren().forPath("/tcc/root/" + globalId);
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    public List<String> findRootErrorId(String globalId){
        try {
            return curatorFramework.getChildren().forPath("/tcc/rootError/" + globalId);
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 找出分支事务提交出错的
     * @param globalId String 全局id
     * @return List<String>
     */
    public List<String> findBranchErrorId(String globalId){
        try {
            return curatorFramework.getChildren().forPath("/tcc/branchError/" + globalId);
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 找出所有分支提交错误事务下是否有存在节点
     * @return boolean
     */
    private boolean doFindExistTransactionWithBranchError(){
        try {
            final boolean root = curatorFramework.getChildren().forPath(String.format("%s/%s", TCC_PATH, "branchError")).isEmpty();
            return !root;
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 找出所有根事务下是否有存在节点
     * @return boolean
     */
    private boolean doFindExistTransaction(){
        try {
            final boolean root = curatorFramework.getChildren().forPath(String.format("%s/%s", TCC_PATH, "rootError")).isEmpty();
            return !root;
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 在根事务中根据路径获取分支事务日志记录
     * @param path String
     * @return Transaction
     */
    public Transaction findByPath(String path){
        try {
            byte[] result = curatorFramework.getData().forPath(path);
            return result != null ? SerializerEngine.deserialize(result,Transaction.class,SerializerEnum.ProtoStuffSerializer) : null;
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 根事务中获取/tcc/branch下所有的分支事务节点
     * @return List<String>
     */
    public List<String> findBranch(){
        try {
            return curatorFramework.getChildren().forPath("/tcc/branch");
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    public List<String> findBranchWithError(){
        try {
            return curatorFramework.getChildren().forPath("/tcc/branchError");
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 根据事务的全局id来获取所有分支事务
     * @param globalId String
     * @return List<String>
     */
    public List<String> findBranchId(String globalId){
        try {
            return curatorFramework.getChildren().forPath("/tcc/branch/" + globalId);
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    public List<String> findBranchIdWithError(String globalId){
        try {
            return curatorFramework.getChildren().forPath("/tcc/branchError/" + globalId);
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 更新分支事务
     * @param transaction Transaction
     * @return int
     */
    public int updateBranch(Transaction transaction){
        try {
            transaction.updateLastUpdateTime();
            transaction.updateVersion();
            curatorFramework.setData()
                    .withVersion(transaction.getVersion() - 2)
                    .forPath(String.format("%s/%s/%s/%s",TCC_PATH,"branch",new String(transaction.getXid().getGlobalTransactionId()),new String(transaction.getXid().getBranchQualifier())),SerializerEngine.serialize(transaction,SerializerEnum.ProtoStuffSerializer));
            return SUCCESS;
        } catch (KeeperException.BadVersionException version) {
            throw new OptimisticLockException("OptimisticLock Bad Version");
        } catch (Exception e){
            throw new TccTransactionException("Tcc Exception",e);
        }
    }

    /**
     * 删除分支事务
     * @param transaction Transaction
     * @return int
     */
    public int deleteBranch(Transaction transaction){
        try {
            curatorFramework.delete()
                    .forPath(String.format("%s/%s/%s/%s",TCC_PATH,"branch",new String(transaction.getXid().getGlobalTransactionId()),new String(transaction.getXid().getBranchQualifier())));
            return SUCCESS;
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }



    /**
     * 根据Type来彻底删除事务日志
     * @param globalTransactionId String
     * @return int
     */
    public int deleteRootBranch(String globalTransactionId){
        try {
            curatorFramework.delete().forPath(String.format("%s/%s/%s",TCC_PATH,"branch",globalTransactionId));
            return SUCCESS;
        } catch (Exception e) {
            throw new TccTransactionException("Tcc Exception",e);
        }
    }



}
