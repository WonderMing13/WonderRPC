package org.wonderming.tcc.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wangdeming
 * @date 2019-11-18 15:03
 **/
@AllArgsConstructor
public enum TransactionType {
    /**
     * 主事务
     */
    ROOT(1),
    /**
     * 分支事务
     */
    BRANCH(2);

    @Getter
    private int id;
}
