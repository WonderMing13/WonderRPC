package org.wonderming.tcc.entity;

import lombok.Data;

import javax.transaction.xa.Xid;
import java.io.Serializable;

/**
 * XA事务协议
 * 一个TM(事务管理器)管理多个RM(资源管理器),每个RM维护自己的事务分支
 * @author wangdeming
 * @date 2019-11-17 14:45
 **/
@Data
public class TransactionXid implements Xid, Serializable {
    /**
     * Xid的格式标识符
     */
    private int formatId = 100;
    /**
     * 全局事务Id
     */
    private byte[] globalTransactionId;
    /**
     * 分支限定Id
     */
    private byte[] branchQualifier;

    public TransactionXid(byte[] globalTransactionId,byte[] branchQualifier){
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = branchQualifier;
    }
}
