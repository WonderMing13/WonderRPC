package org.wonderming.tcc.tccimpl;

import lombok.extern.slf4j.Slf4j;
import org.wonderming.config.thread.MyThreadFactory;
import org.wonderming.exception.TccTransactionException;
import org.wonderming.tcc.entity.Transaction;
import org.wonderming.tcc.entity.TransactionContext;
import org.wonderming.tcc.entity.TransactionXid;
import org.wonderming.tcc.type.TransactionStatus;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.TransactionManager;
import org.wonderming.tcc.type.TransactionType;
import org.wonderming.utils.SnowflakeIdWorkerUtil;

/**
 * @author wangdeming
 * @date 2019-11-18 21:57
 **/
@Slf4j
public class DefaultTransactionManager implements TransactionManager {
    /**
     * 定义当前线程的事务局部变量.
     */
    private ThreadLocal<Transaction> threadLocalTransaction = new ThreadLocal<>();
    /**
     * 事务存储器
     */
    private TransactionConfiguration transactionConfiguration;

    public DefaultTransactionManager(TransactionConfiguration transactionConfiguration) {
        this.transactionConfiguration = transactionConfiguration;
    }

    @Override
    public void begin() {
        TransactionXid xid = new TransactionXid(SnowflakeIdWorkerUtil.newId(), SnowflakeIdWorkerUtil.newId());
        Transaction transaction = new Transaction(xid, TransactionStatus.TRY, TransactionType.ROOT);
        int result = transactionConfiguration.getResourceManager().create(transaction);
        log.info("create root transaction result:{}", result);
        threadLocalTransaction.set(transaction);
    }

    @Override
    public void begin(TransactionContext transactionContext) {
        TransactionXid transactionXid = transactionContext.getXid();
        TransactionStatus status = transactionContext.getStatus();
        Transaction transaction = new Transaction(transactionXid, status, TransactionType.BRANCH);
        int result = transactionConfiguration.getResourceManager().create(transaction);
        log.info("create branch transaction result:{}", result);
        threadLocalTransaction.set(transaction);
    }

    @Override
    public void changeTransactionStatus(TransactionContext transactionContext) {
        TransactionXid transactionXid = transactionContext.getXid();
        Transaction transactionQuery = new Transaction(transactionXid, null, null);
        Transaction transaction = transactionConfiguration.getResourceManager().findByXid(transactionQuery);
        if (transaction != null) {
            transaction.setStatus(transactionContext.getStatus());
            threadLocalTransaction.set(transaction);
        } else {
            throw new TccTransactionException("Tcc Not Exist");
        }
    }

    @Override
    public Transaction getCurrentTransaction() {
        return threadLocalTransaction.get();
    }

    @Override
    public void commit(Transaction transaction) {
        transaction.setStatus(TransactionStatus.CONFIRM);
        final int update = transactionConfiguration.getResourceManager().update(transaction);
        log.info("update Root transaction {},result:{}",transaction,update);
        try {
            transaction.commit(transaction);
            int result = transactionConfiguration.getResourceManager().delete(transaction);
            log.info("delete Root transaction {},result:{}", transaction, result);
//            final int rootBranch = transactionConfiguration.getResourceManager().deleteRootBranch(new String(transaction.getXid().getGlobalTransactionId()),"root");
//            log.info("delete Root Branch result:{}",rootBranch);
            threadLocalTransaction.remove();
        } catch (Throwable commitException) {
            throw new TccTransactionException("Commit error", commitException);
        }
    }

    @Override
    public void rollback(Transaction transaction) {
        transaction.setStatus(TransactionStatus.CANCEL);
        transactionConfiguration.getResourceManager().update(transaction);
        try {
            transaction.rollback(transaction);
            int result = transactionConfiguration.getResourceManager().delete(transaction);
            log.info("delete transaction {},result:{}", transaction, result);
//            final int rootBranch = transactionConfiguration.getResourceManager().deleteRootBranch(new String(transaction.getXid().getGlobalTransactionId()),"root");
//            log.info("delete Root Branch result:{}",rootBranch);
            threadLocalTransaction.remove();
        } catch (Throwable rollbackException) {
            throw new TccTransactionException("Rollback error", rollbackException);
        }
    }
}
