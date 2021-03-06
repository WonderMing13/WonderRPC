package org.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.wonderming.annotation.EnableWonderRpc;

/**
 * @author wangdeming
 **/
@SpringBootApplication
@EnableWonderRpc(basePackages = "org.provider.service")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class,args);
    }
}
