//package org.wonderming.entity;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @className: HelloAutoConfiguration
// * @package: org.wonderming.entity
// * @author: wangdeming
// * @date: 2019-09-06 15:04
// **/
//@Configuration
//@EnableConfigurationProperties(HelloProperties.class)
//@ConditionalOnClass(HelloService.class)
//public class HelloAutoConfiguration {
//
//    @Autowired
//    private HelloProperties helloProperties;
//
//    @Bean
//    @ConditionalOnMissingBean(HelloService.class)
//    public HelloService helloService(){
//        System.out.println("=====================赖斯够=================");
//        HelloService helloService = new HelloService();
//        helloService.setMsg(helloProperties.getMsg());
//        helloService.setShow(helloProperties.isShow());
//        return helloService;
//    }
//}
