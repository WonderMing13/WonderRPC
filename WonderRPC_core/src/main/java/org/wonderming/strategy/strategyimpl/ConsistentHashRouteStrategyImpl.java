package org.wonderming.strategy.strategyimpl;



import org.wonderming.strategy.IRouteStrategy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性hash负载均衡
 * @author wangdeming
 * @date 2019-11-06 09:56
 **/
public class ConsistentHashRouteStrategyImpl implements IRouteStrategy {

    private static final long FNV_32_INIT = 2166136261L;

    private static final int FNV_32_PRIME = 16777619;

    private static final int VIRTUAL_NODES = 5;

    private static SortedMap<Integer,String> virtualNodes = new TreeMap<>();

    /**
     * 获取hashcode
     * @param ipStr 获取到NettyServer的ip
     * @return int
     */
    private int getHashCode(String ipStr){
        int hash = (int) FNV_32_INIT;
        for (int i = 0; i < ipStr.length(); i++) {
            hash = (hash ^ ipStr.charAt(i)) * FNV_32_PRIME;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        hash = Math.abs(hash);
        return hash;
    }

    @Override
    public <T> T select(List<T> serviceList) {
        serviceList.forEach(s->{
            //一个真实节点对应5个虚拟节点
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                String virtualNodeName = s + "&&VirtualNode" + i;
                int hash = getHashCode(virtualNodeName);
                virtualNodes.put(hash,virtualNodeName);
            }
        });
        try {
            //获取真实ip地址
            final String ipStr = InetAddress.getLocalHost().getHostAddress();
            int hash = getHashCode(ipStr);
            //得到大于hash值的map
            SortedMap<Integer,String> sortedMap = virtualNodes.tailMap(hash);
            //取到最近的一个key值并返回对应的虚拟节点名称
            final String virtualNodeName = sortedMap.get(sortedMap.firstKey());
            String serverAddress = virtualNodeName.substring(0,virtualNodeName.indexOf("&&"));
            final Optional<T> firstOptional = serviceList.stream().filter(s -> s.equals(serverAddress)).findFirst();
            //留坑..先返回null吧
            return firstOptional.orElse(null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
