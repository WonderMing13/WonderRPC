package org.wonderming.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wangdeming
 * @date 2019-10-15 17:12
 **/
@Data
@Component
@ConfigurationProperties(prefix = "wonder.netty.client")
public class NettyClientProperties {
    private String name;
    private String host;
    private int port;
}
