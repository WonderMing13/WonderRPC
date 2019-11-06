package org.wonderming.strategy.strategyimpl;

import org.apache.commons.lang3.RandomUtils;
import org.wonderming.strategy.IRouteStrategy;

import java.util.List;
import java.util.Random;

/**
 * 随机算法负载均衡
 * @author wangdeming
 * @date 2019-11-05 15:37
 **/
public class RandomRouteStrategyImpl implements IRouteStrategy {

    @Override
    public <T> T select(List<T> serviceList) {
        return serviceList.get(RandomUtils.nextInt(0,serviceList.size()));
    }
}
