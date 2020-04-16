package org.wonderming.tcc.tccimpl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.wonderming.config.properties.TccProperties;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.entity.Participant;
import org.wonderming.tcc.entity.Transaction;
import org.wonderming.tcc.type.TransactionStatus;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author wangdeming
 * @date 2019-12-09 15:50
 **/
@Slf4j
@EnableConfigurationProperties(TccProperties.class)
public class DefaultTransactionRecovery {

    @Autowired
    private TccProperties tccProperties;

    private TransactionConfiguration transactionConfiguration;

    private static final String ROOT = "root";

    public TccProperties getTccProperties() {
        return tccProperties;
    }

    public void setTransactionConfiguration(TransactionConfiguration transactionConfiguration) {
        this.transactionConfiguration = transactionConfiguration;
    }

    /**
     * 对象锁,锁住资源
     */
    public synchronized void startRecover() {
        log.info("Starting Recover....");
        long timeBefore = System.currentTimeMillis() - tccProperties.getRecoveryDuration() * 1000;
        //root事务confirm或者cancel出错的事务
        final List<Transaction> rootTransactionList = transactionConfiguration.getResourceManager().doFindAllUnmodified(new Date(timeBefore));
        //branch事务confirm或者cancel出错的事务
        final List<Transaction> branchErrorTransactionList = transactionConfiguration.getResourceManager().doFindAllUnmodifiedWithBranchError(new Date(timeBefore));
        //针对root confirm和cancel的错误异常处理
        if (!rootTransactionList.isEmpty() && tccProperties.getType().equals(ROOT)) {
            List<Transaction> transactionList = Lists.newArrayList();
            rootTransactionList.forEach(transaction -> {
                log.info("transaction is {}", transaction);
                //更新时间防止其他JVM获取到
                final int update = transactionConfiguration.getResourceManager().updateWithRootError(transaction);
                if (update > 0) {
                    transactionList.add(transaction);
                }
            });
            for (Transaction transaction:transactionList) {
                AtomicReference<String> targetName = new AtomicReference<>();
                AtomicReference<String> methodName = new AtomicReference<>();;
                final List<Participant> participants = transaction.getParticipants();
                participants.forEach(participant -> {
                    if (transaction.getStatus().name().equals(TransactionStatus.CONFIRM.name())){
                        targetName.set(participant.getConfirmContext().getTargetClassName());
                        methodName.set(participant.getConfirmContext().getMethodName());
                    }else if (transaction.getStatus().name().equals(TransactionStatus.CANCEL.name())){
                        targetName.set(participant.getConfirmContext().getTargetClassName());
                        methodName.set(participant.getConfirmContext().getMethodName());
                    }
                });
                //超过重复次数的跳过次循环,说明重复次数到头进行人工干预
                if (transaction.getRetriedCount() > tccProperties.getMaxRetryCount()){
                    log.error(String.format("recover failed with max retry count, will not try again. global id:%s, branch id:%s,target name:%s,method name:%s, status:%s, retried count:%d",
                            new String(transaction.getXid().getGlobalTransactionId()),
                            new String(transaction.getXid().getBranchQualifier()),
                            targetName.get(),
                            methodName.get(),
                            transaction.getStatus().name(),
                            transaction.getRetriedCount()));
                    final int deleteWithRootError = transactionConfiguration.getResourceManager().deleteWithRootError(transaction);
                    continue;
                }
                transaction.addRetriedCount();
                if (transaction.getStatus() == TransactionStatus.CONFIRM) {
                    //更新新增的重试次数
                    transactionConfiguration.getResourceManager().updateWithRootError(transaction);
                    //提交root事务的本地事务
                    transaction.nativeCommit(transaction);
                } else if (transaction.getStatus() == TransactionStatus.CANCEL) {
                    //更新新增的重试次数
                    transactionConfiguration.getResourceManager().updateWithRootError(transaction);
                    //回滚root事务的本地事务
                    transaction.nativeRollback(transaction);
                }
            }
        }
        //针对branch事务confirm或者cancel出错的事务
        if (!branchErrorTransactionList.isEmpty() && tccProperties.getType().equals(ROOT)) {
            List<Transaction> transactionList = Lists.newArrayList();
            branchErrorTransactionList.forEach(transaction -> {
                log.info("transaction is {}", transaction);
                final int update = transactionConfiguration.getResourceManager().updateWithBranchError(transaction);
                if (update > 0) {
                    transactionList.add(transaction);
                }
            });
            for (Transaction transaction:transactionList) {
                //超过重复次数的跳过次循环,说明重复次数到头进行人工干预
                if (transaction.getRetriedCount() > tccProperties.getMaxRetryCount()){
                    AtomicReference<String> targetName = new AtomicReference<>();
                    AtomicReference<String> methodName = new AtomicReference<>();;
                    final List<Participant> participants = transaction.getParticipants();
                    participants.forEach(participant -> {
                        if (transaction.getStatus().name().equals(TransactionStatus.CONFIRM.name())){
                            targetName.set(participant.getConfirmContext().getTargetClassName());
                            methodName.set(participant.getConfirmContext().getMethodName());
                        }else if (transaction.getStatus().name().equals(TransactionStatus.CANCEL.name())){
                            targetName.set(participant.getConfirmContext().getTargetClassName());
                            methodName.set(participant.getConfirmContext().getMethodName());
                        }
                    });
                    log.error(String.format("recover failed with max retry count, will not try again. global id:%s, branch id: %s,target name: %s,method name:%s, status:%s, retried count:%d",
                            new String(transaction.getXid().getGlobalTransactionId()),
                            new String(transaction.getXid().getBranchQualifier()),
                            targetName.get(),
                            methodName.get(),
                            transaction.getStatus().name(),
                            transaction.getRetriedCount()));
                    //删除日志
                    final int deleteWithBranchError = transactionConfiguration.getResourceManager().deleteWithBranchError(transaction);
                    continue;
                }
                transaction.addRetriedCount();
                if (transaction.getStatus() == TransactionStatus.CONFIRM) {
                    //更新新增的重试次数
                    transactionConfiguration.getResourceManager().updateWithBranchError(transaction);
                    //提交root事务的本地事务
                    transaction.remoteCommit(transaction);
                } else if (transaction.getStatus() == TransactionStatus.CANCEL) {
                    //更新新增的重试次数
                    transactionConfiguration.getResourceManager().updateWithBranchError(transaction);
                    //回滚root事务的本地事务
                    transaction.remoteRollback(transaction);
                }
            }
        }
        log.info("end Recover....");
    }
}
