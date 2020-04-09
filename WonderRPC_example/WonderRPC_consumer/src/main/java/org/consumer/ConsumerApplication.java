package org.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.wonderming.annotation.EnableWonderRpc;

/**
 * @author wangdeming
 **/
@SpringBootApplication
@EnableWonderRpc(basePackages = "org.consumer.api")
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class,args);
    }
}
