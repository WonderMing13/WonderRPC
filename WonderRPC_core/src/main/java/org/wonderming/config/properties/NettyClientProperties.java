package org.wonderming.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.wonderming.strategy.RouteEnum;

/**
 * @author wangdeming
 * @date 2019-10-15 17:12
 **/
@Data
@ConfigurationProperties(prefix = "wonder.netty.client")
public class NettyClientProperties {
    private String name;
    private RouteEnum routeStrategy = RouteEnum.Random;
}
