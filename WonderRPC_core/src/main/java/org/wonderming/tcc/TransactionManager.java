package org.wonderming.tcc;

import org.wonderming.tcc.entity.Transaction;
import org.wonderming.tcc.entity.TransactionContext;
import org.wonderming.tcc.type.TransactionStatus;

/**
 * 全局事务管理器TM
 * @author wangdeming
 * @date 2019-11-18 09:59
 **/
public interface TransactionManager {
    /**
     * 开始一个tcc Root事务
     */
    void begin();

    /**
     * 开始一个tcc 分支事务
     * @param transactionContext TransactionContext
     */
    void begin(TransactionContext transactionContext);

    /**
     * 改变当前事务状态
     * @param transactionContext TransactionContext
     */
    void changeTransactionStatus(TransactionContext transactionContext);

    /**
     * 获取当前事务
     * @return Transaction
     */
    Transaction getCurrentTransaction();

    /**
     * 对参与者进行事务提交
     */
    void commit(Transaction transaction);

    /**
     * 对参与者进行事务回滚
     */
    void rollback(Transaction transaction);
}
