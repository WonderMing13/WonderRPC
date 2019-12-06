package org.wonderming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.wonderming.annotation.EnableWonderRpc;

/**
 * @author wangdeming
 * @date 2019-09-06 15:15
 **/
@EnableWonderRpc(basePackages = "org.wonderming.service")
@SpringBootApplication
public class CommonApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommonApplication.class,args);
    }
}
