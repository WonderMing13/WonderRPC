package org.wonderming.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wangdeming
 * @date 2019-12-05 10:53
 **/
@Data
@ConfigurationProperties(prefix = "wonder.tcc")
public class TccProperties {
    private String type;
}
