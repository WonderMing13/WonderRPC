package org.wonderming.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wangdeming
 * @date 2019-09-16 16:01
 **/
@Data
@ConfigurationProperties(prefix = "wonder.zk")
public class ZookeeperProperties {
    /**
     * 集群模式用逗号隔开
     */
    private String address;
    private int maxRetries = 3;
    private int baseSleepTimeMs = 1000;
}
