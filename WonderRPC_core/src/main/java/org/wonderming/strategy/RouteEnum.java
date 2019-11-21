package org.wonderming.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wangdeming
 * @date 2019-11-06 14:18
 **/
@AllArgsConstructor
public enum  RouteEnum {
    //随机算法
    Random("random"),
    //一致性hash算法
    ConsistentHash("hash");

    @Getter
    private String name;

}
