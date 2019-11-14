package org.wonderming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.wonderming.annotation.EnableWonderDts;
import org.wonderming.annotation.EnableWonderRpc;


/**
 * @className: TestApplication
 * @package: org.wonderming
 * @author: wangdeming
 * @date: 2019-09-11 10:01
 **/
@EnableWonderDts
@EnableWonderRpc(basePackages = "org.wonderming.service")
@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class,args);
    }
}
