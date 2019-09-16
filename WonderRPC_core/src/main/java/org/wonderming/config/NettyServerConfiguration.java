package org.wonderming.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangdeming
 * @date 2019-09-16 17:18
 **/
@Configuration
@EnableConfigurationProperties(NettyServerProperties.class)
public class NettyServerConfiguration {
}
