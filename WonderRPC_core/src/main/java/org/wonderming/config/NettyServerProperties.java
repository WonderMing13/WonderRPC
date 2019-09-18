package org.wonderming.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wangdeming
 * @date 2019-09-16 17:18
 **/
@Data
@Component
@ConfigurationProperties(prefix = "wonder.netty.server")
public class NettyServerProperties {
    private String host;
    private int port;
}
