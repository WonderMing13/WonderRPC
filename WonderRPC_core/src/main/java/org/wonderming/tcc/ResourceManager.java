package org.wonderming.tcc;

import org.wonderming.tcc.entity.Transaction;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 事务资源管理器
 *
 * @author wangdeming
 * @date 2019-11-18 21:29
 **/
public interface ResourceManager {
    /**
     * 创建事务日志记录
     *
     * @param transaction Transaction
     * @return rowEffected
     */
    int create(Transaction transaction);

    /**
     * 更新事务日志记录
     *
     * @param transaction Transaction
     * @return rowEffected
     */
    int update(Transaction transaction);

    /**
     *  分支错误事务日志记录
     * @param transaction Transaction
     * @return rowEffected
     */
    int updateWithBranchError(Transaction transaction);

    /**
     * 更新主commit/error错误
     * @param transaction Transaction
     * @return rowEffected
     */
    int updateWithRootError(Transaction transaction);

    /**
     * 删除事务日志记录
     *
     * @param transaction Transaction
     * @return rowEffected
     */
    int delete(Transaction transaction);

    /**
     * 删除分支错误事务日志记录
     * @param transaction Transaction
     * @return rowEffected
     */
    int deleteWithBranchError(Transaction transaction);

    int deleteWithRootError(Transaction transaction);

    /**
     * 根据Xid获取事务日志记录
     *
     * @param transaction Transaction
     * @return Transaction
     */
    Transaction findByXid(Transaction transaction);

    /**
     * 获取超过持续时间的事务
     *
     * @param date Date
     * @return List<Transaction>
     */
    List<Transaction> doFindAllUnmodified(Date date);

    /**
     * 获取分支confirm cancel超过持续时间的异常事务
     *
     * @param date Date
     * @return List<Transaction>
     */
    List<Transaction> doFindAllUnmodifiedWithBranchError(Date date);
}
