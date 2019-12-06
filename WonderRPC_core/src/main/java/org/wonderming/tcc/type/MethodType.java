package org.wonderming.tcc.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 方法类型
 * @author wangdeming
 * @date 2019-11-18 22:03
 **/
@AllArgsConstructor
public enum MethodType {
    /**
     * 根事务方法(表示一个主事务的发起者）
     */
    ROOT(1),
    /**
     * 提供者 （表示登记的分支事务的具体实现类，用于发起分支事务的具体实现）
     */
    PROVIDER(2),
    /**
     * 不是一个具体事务方法
     */
    NORMAL(3);

    @Getter
    private int id;
}
