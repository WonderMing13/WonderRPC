package org.wonderming.annotation;

import org.springframework.context.annotation.Import;
import org.wonderming.config.configuration.RabbitConfiguration;

import java.lang.annotation.*;

/**
 * @author wangdeming
 * @date 2019-11-12 11:06
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Import({RabbitConfiguration.class})
public @interface EnableWonderDts {
}
