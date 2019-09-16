package org.wonderming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.wonderming.annotation.EnableWonderRpc;

/**
 * @className: TestApplication
 * @package: org.wonderming
 * @author: wangdeming
 * @date: 2019-09-11 10:01
 **/
@SpringBootApplication
@EnableWonderRpc(basePackages = "org.wonderming.service")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class,args);
    }
}
