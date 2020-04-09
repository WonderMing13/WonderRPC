package org.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.wonderming.annotation.EnableWonderRpc;

/**
 * @author wangdeming
 **/
@SpringBootApplication
@EnableWonderRpc(basePackages = "org.consumer.api")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class,args);
    }
}
