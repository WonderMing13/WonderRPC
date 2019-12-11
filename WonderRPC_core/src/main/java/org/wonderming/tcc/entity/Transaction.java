package org.wonderming.tcc.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.wonderming.config.client.NettyClient;
import org.wonderming.config.configuration.ServiceConfiguration;
import org.wonderming.entity.DefaultFuture;
import org.wonderming.entity.RpcRequest;
import org.wonderming.entity.RpcResponse;
import org.wonderming.exception.InvokeException;
import org.wonderming.tcc.type.TransactionStatus;
import org.wonderming.tcc.type.TransactionType;
import org.wonderming.utils.ApplicationContextUtil;
import org.wonderming.utils.SnowflakeIdWorkerUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangdeming
 * @date 2019-11-18 10:02
 **/
@Slf4j
@Data
public class Transaction implements Serializable {
    /**
     * 事务Xid
     */
    private TransactionXid xid;
    /**
     * 事务状态
     */
    private TransactionStatus status;
    /**
     * 事务类型(1:主事务，2:分支事务)
     */
    private TransactionType transactionType;
    /**
     * 创建时间
     */
    private Date createTime = new Date();
    /**
     * 最后更新时间
     */
    private Date lastUpdateTime = new Date();
    /**
     * 参与者列表(一个confirm方法和一个cancel方法)
     */
    private List<Participant> participants = new ArrayList<>();
    /**
     * 乐观锁的版本控制
     */
    private int version = 1;
    /**
     * 更新最后更新时间
     */
    public void updateLastUpdateTime(){
        this.lastUpdateTime = new Date();
    }
    /**
     * 更新版本号
     */
    public void updateVersion(){
        this.version++;
    }

    /**
     * 默认构造函数
     */
    public Transaction(){}

    /**
     * 构造函数
     * @param transactionXid TransactionXid XA分布式Id
     * @param status 事务状态
     * @param type TransactionType
     */
    public Transaction(TransactionXid transactionXid, TransactionStatus status, TransactionType type) {
        this.xid=transactionXid;
        this.status=status;
        this.transactionType=type;
    }

    /**
     * 事务提交
     */
    public void commit(Transaction transaction) {
        //远程事务提交
        final ServiceConfiguration serviceConfiguration = ApplicationContextUtil.getBean(ServiceConfiguration.class);
        final NettyClient nettyClient = ApplicationContextUtil.getBean(NettyClient.class);
        final List<String> branchList = serviceConfiguration.findBranch();
        //全局唯一根事务id
        final String rootGlobalTransactionId = branchList.stream().filter(s -> s.equals(new String(transaction.xid.getGlobalTransactionId()))).collect(Collectors.toList()).get(0);
        //该根事务下的所有分支事务
        final List<String> branchIdList = serviceConfiguration.findBranchId(rootGlobalTransactionId);
        branchIdList.forEach(a->{
            String path = String.format("%s/%s/%s/%s","/tcc","branch",rootGlobalTransactionId,a);
            final Transaction transactionZk = serviceConfiguration.findByPath(path);
            transactionZk.setStatus(TransactionStatus.CONFIRM);
            final int update = serviceConfiguration.updateBranch(transactionZk);
            log.info("update Branch transaction {},result:{}",transactionZk,update);
            transactionZk.getParticipants().forEach(p->{
                InvocationContext confirmContext = p.getConfirmContext();
                remoteInvoke(nettyClient, confirmContext);
            });
            final int delete = serviceConfiguration.deleteBranch(transactionZk);
            log.info("delete Branch transaction {},result:{}", transactionZk, delete);
        });
        final int rootBranch = serviceConfiguration.deleteRootBranch(rootGlobalTransactionId,"branch");
        log.info("delete Root Branch result:{}",rootBranch);
        //本地事务提交
        for (Participant participant : this.participants) {
            participant.commit();
        }
    }

    /**
     * 事务回滚
     */
    public void rollback(Transaction transaction) {
        //远程事务回滚
        final ServiceConfiguration serviceConfiguration = ApplicationContextUtil.getBean(ServiceConfiguration.class);
        final NettyClient nettyClient = ApplicationContextUtil.getBean(NettyClient.class);
        final List<String> branchList = serviceConfiguration.findBranch();
        final String rootGlobalTransactionId = branchList.stream().filter(s -> s.equals(new String(transaction.xid.getGlobalTransactionId()))).collect(Collectors.toList()).get(0);
        final List<String> branchIdList = serviceConfiguration.findBranchId(rootGlobalTransactionId);
        branchIdList.forEach(a->{
            String path = String.format("%s/%s/%s/%s","/tcc","branch",rootGlobalTransactionId,a);
            final Transaction transactionZk = serviceConfiguration.findByPath(path);
            transactionZk.setStatus(TransactionStatus.CANCEL);
            final int update = serviceConfiguration.updateBranch(transactionZk);
            log.info("update Branch transaction {},result:{}",transactionZk,update);
            transactionZk.getParticipants().forEach(p->{
                final InvocationContext cancelContext = p.getCancelContext();
                remoteInvoke(nettyClient, cancelContext);
            });
            final int delete = serviceConfiguration.deleteBranch(transactionZk);
            log.info("delete Branch transaction {},result:{}", transactionZk, delete);
        });
        final int rootBranch = serviceConfiguration.deleteRootBranch(rootGlobalTransactionId,"branch");
        log.info("delete Root Branch result:{}",rootBranch);
        //本地事务回滚
        for (Participant participant : participants) {
            participant.rollback();
        }
    }

    private void remoteInvoke(NettyClient nettyClient, InvocationContext invocationContext) {
        final RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(SnowflakeIdWorkerUtil.getInstance().nextId())
                .setInterfaceName(invocationContext.getTargetClassName())
                .setParameterTypes(invocationContext.getParameterTypes())
                .setParam(invocationContext.getParam())
                .setMethodName(invocationContext.getMethodName());
        try {
            final DefaultFuture defaultFuture = nettyClient.start(rpcRequest);
            //远程调用超过5s视为远程commit错误
            final RpcResponse rpcResponse = defaultFuture.get(5000);
        } catch (Exception e) {
            throw new InvokeException("remote invoke error",e);
        }
    }

    /**
     * 添加事务参与者
     * @param participant 事务参与者
     */
    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

}
