package org.wonderming.tcc.tccimpl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.wonderming.config.properties.TccProperties;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.entity.Transaction;
import org.wonderming.tcc.type.TransactionStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
                //更新时间防止其他JVM获取到
                final int update = transactionConfiguration.getResourceManager().update(transaction);
                if (update > 0) {
                    transactionList.add(transaction);
                }
            });
            for (Transaction transaction:transactionList) {
                //超过重复次数的跳过次循环,说明重复次数到头进行人工干预
                if (transaction.getRetriedCount() > tccProperties.getMaxRetryCount()){
                    log.error(String.format("recover failed with max retry count, will not try again. txid:%s, status:%s, retried count:%d", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount()));
                    continue;
                }
                transaction.addRetriedCount();
                if (transaction.getStatus() == TransactionStatus.CONFIRM) {
                    //更新新增的重试次数
                    transactionConfiguration.getResourceManager().update(transaction);
                    //提交root事务的本地事务
                    transaction.nativeCommit(transaction);
                } else if (transaction.getStatus() == TransactionStatus.CANCEL) {
                    //更新新增的重试次数
                    transactionConfiguration.getResourceManager().update(transaction);
                    //回滚root事务的本地事务
                    transaction.nativeRollback(transaction);
                }
                final int delete = transactionConfiguration.getResourceManager().delete(transaction);
                log.info("result is {}", delete);
            }
        }
        //针对branch事务confirm或者cancel出错的事务
        if (!branchErrorTransactionList.isEmpty() && tccProperties.getType().equals(ROOT)) {
            List<Transaction> transactionList = Lists.newArrayList();
            branchErrorTransactionList.forEach(transaction -> {
                System.out.println(transaction);
                final int update = transactionConfiguration.getResourceManager().updateWithBranchError(transaction);
                if (update > 0) {
                    transactionList.add(transaction);
                }
            });
            for (Transaction transaction:transactionList) {
                //超过重复次数的跳过次循环,说明重复次数到头进行人工干预
                if (transaction.getRetriedCount() > tccProperties.getMaxRetryCount()){
                    log.error(String.format("recover failed with max retry count, will not try again. txid:%s, status:%s, retried count:%d", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount()));
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
