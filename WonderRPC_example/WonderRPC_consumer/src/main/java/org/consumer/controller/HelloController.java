package org.consumer.controller;

import org.consumer.api.ConsumerHelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wangdeming
 **/
@RestController
public class HelloController {

    @Resource
    private ConsumerHelloService consumerHelloService;

    @GetMapping(value = "/test")
    public String getTest(){
        return consumerHelloService.test();
    }
}
