package org.wonderming.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wangdeming
 * @date 2019-09-16 17:18
 **/
@Data
@ConfigurationProperties(prefix = "wonder.netty.server")
public class NettyServerProperties {
    private String host;
    private int port;
}
