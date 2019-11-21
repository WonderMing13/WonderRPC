package org.wonderming.tcc.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 事务的当前状态
 * @author wangdeming
 * @date 2019-11-17 15:03
 **/
@AllArgsConstructor
public enum TransactionStatus implements Serializable {
    /**
     * 尝试:1
     */
    TRY(1),
    /**
     * 确认:2
     */
    CONFIRM(2),
    /**
     * 取消:3
     */
    CANCEL(3);

    @Getter
    private int id;
}
