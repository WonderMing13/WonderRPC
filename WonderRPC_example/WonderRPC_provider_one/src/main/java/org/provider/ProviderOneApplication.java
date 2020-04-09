package org.provider;

import org.provider.service.ProviderOneTestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.wonderming.annotation.EnableWonderRpc;

/**
 * @author wangdeming
 **/
@SpringBootApplication
@EnableWonderRpc(basePackages = "org.provider.service.impl")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ProviderOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderOneApplication.class,args);
    }
}
