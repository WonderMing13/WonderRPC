package org.wonderming.tcc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.wonderming.tcc.type.TransactionStatus;

import java.io.Serializable;

/**
 * Tcc分布式事务上下文
 * @author wangdeming
 * @date 2019-11-17 15:11
 **/
@Data
@AllArgsConstructor
public class TransactionContext implements Serializable {
    /**
     * 事务Xid
     */
    private TransactionXid xid;
    /**
     * 事务状态
     */
    private TransactionStatus status;
}
