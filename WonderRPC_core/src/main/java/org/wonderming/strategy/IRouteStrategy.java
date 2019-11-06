package org.wonderming.strategy;

import java.util.List;

/**
 * @author wangdeming
 * @date 2019-11-05 15:34
 **/
public interface IRouteStrategy {

    /**
     * 负载均衡策略
     * @param serviceList 服务列表
     * @param <T> 数据类型
     * @return 返回的数据类型
     */
    <T> T select(List<T> serviceList);
}
