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
    /**
     * 定时任务重试最多次数
     */
    private int maxRetryCount = 5;
    /**
     * 定时任务执行间隔时间
     */
    private int recoveryDuration = 120;
    /**
     * 每隔一分钟执行一次
     */
    private String cronExpression = "0 */1 * * * ?";
}
