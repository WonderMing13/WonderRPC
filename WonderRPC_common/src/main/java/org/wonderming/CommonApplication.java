package org.wonderming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.wonderming.annotation.EnableWonderRpc;

/**
 * @className: CommonApplication
 * @package: org.wonderming
 * @author: wangdeming
 * @date: 2019-09-06 15:15
 **/
@SpringBootApplication
@EnableWonderRpc(basePackages = "org.wonderming")
public class CommonApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommonApplication.class,args);
    }
}
