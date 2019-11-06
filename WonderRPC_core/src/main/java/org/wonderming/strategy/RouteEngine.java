package org.wonderming.strategy;

import com.google.common.collect.Maps;
import org.wonderming.serializer.ISerializer;
import org.wonderming.serializer.SerializerEnum;
import org.wonderming.strategy.strategyimpl.ConsistentHashRouteStrategyImpl;
import org.wonderming.strategy.strategyimpl.RandomRouteStrategyImpl;

import java.util.Map;

/**
 * @author wangdeming
 * @date 2019-11-06 14:16
 **/
public class RouteEngine {

    private static final Map<RouteEnum, IRouteStrategy> ROUTE_MAP = Maps.newConcurrentMap();

    static {
        ROUTE_MAP.put(RouteEnum.Random,new RandomRouteStrategyImpl());
        ROUTE_MAP.put(RouteEnum.ConsistentHash,new ConsistentHashRouteStrategyImpl());
    }

    public static IRouteStrategy queryStrategy(RouteEnum routeEnum){
        return ROUTE_MAP.get(routeEnum);
    }
}
