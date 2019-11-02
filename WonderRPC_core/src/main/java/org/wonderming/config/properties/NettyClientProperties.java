package org.wonderming.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wangdeming
 * @date 2019-10-15 17:12
 **/
@Data
@ConfigurationProperties(prefix = "wonder.netty.client")
public class NettyClientProperties {
    private String name;
}
