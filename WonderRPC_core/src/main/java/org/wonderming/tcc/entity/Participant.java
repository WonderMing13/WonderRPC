package org.wonderming.tcc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wangdeming
 * @date 2019-11-18 10:07
 **/
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class Participant implements Serializable {
    /**
     * 事务Xid
     */
    private TransactionXid xid;
    /**
     * 确认调用的上下文
     */
    private InvocationContext confirmContext;
    /**
     * 取消调用的上下文
     */
    private InvocationContext cancelContext;
    /**
     * 提交参与者事务
     */
     void commit() throws Exception {
        confirmContext.invoke();
    }
    /**
     * 回滚参与者事务
     */
    void rollback() throws Exception {
        cancelContext.invoke();
    }

    public Participant(){}

}
