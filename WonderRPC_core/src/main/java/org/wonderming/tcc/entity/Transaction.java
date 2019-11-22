package org.wonderming.tcc.entity;

import lombok.Data;
import org.wonderming.tcc.type.TransactionStatus;
import org.wonderming.tcc.type.TransactionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangdeming
 * @date 2019-11-18 10:02
 **/
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
     * 事务下次处理时间,事务恢复时查该时间
     */
    private Date nextProcessTime = new Date();
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

    public Transaction(TransactionXid transactionXid, TransactionStatus status, TransactionType type) {
        this.xid=transactionXid;
        this.status=status;
        this.transactionType=type;
    }

    /**
     * 事务提交
     */
    public void commit() {
        for (Participant participant : participants) {
            participant.commit();
        }
    }

    /**
     * 事务回滚
     */
    public void rollback() {
        for (Participant participant : participants) {
            participant.rollback();
        }
    }

}
