package org.wonderming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.wonderming.annotation.EnableWonderRpc;

/**
 * @author wangdeming
 * @date 2019-11-16 15:51
 **/
@EnableWonderRpc(basePackages = "org.wonderming.service")
@SpringBootApplication
public class TwoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TwoApplication.class,args);
    }
}
