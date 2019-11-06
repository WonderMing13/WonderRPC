package org.wonderming.strategy;

/**
 * @author wangdeming
 * @date 2019-11-06 14:18
 **/
public enum  RouteEnum {
    //随机算法
    Random("random"),
    //一致性hash算法
    ConsistentHash("hash");

    private String name;

    RouteEnum(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }}
