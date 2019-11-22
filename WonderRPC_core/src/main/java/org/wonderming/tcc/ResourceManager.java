package org.wonderming.tcc;

import org.wonderming.tcc.entity.Transaction;

/**
 * 事务资源管理器
 * @author wangdeming
 * @date 2019-11-18 21:29
 **/
public interface ResourceManager {
    /**
     * 创建事务日志记录
     * @param transaction Transaction
     * @return rowEffected
     */
    int create(Transaction transaction);
    /**
     * 更新事务日志记录
     * @param transaction Transaction
     * @return rowEffected
     */
    int update(Transaction transaction);
    /**
     * 删除事务日志记录
     * @param transaction Transaction
     * @return rowEffected
     */
    int delete(Transaction transaction);

    /**
     * 根据Xid获取事务日志记录
     * @param transaction Transaction
     * @return Transaction
     */
    Transaction findByXid(Transaction transaction);
}
