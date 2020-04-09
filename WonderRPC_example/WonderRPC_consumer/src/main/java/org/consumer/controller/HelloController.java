package org.consumer.controller;

import org.consumer.api.ConsumerOneTestService;
import org.consumer.api.service.ConsumerTccService;
import org.consumer.api.ConsumerTestService;
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
    private ConsumerOneTestService consumerOneTestService;

    @Resource
    private ConsumerTestService consumerTestService;

    @Autowired
    private ConsumerTccService consumerTccService;

    @GetMapping(value = "/testRpc")
    public String getTest(){
        return consumerOneTestService.test(null);
    }

    @GetMapping(value = "/testRpcOne")
    public void getTestOne(){
       consumerTestService.test(null);
    }

    @GetMapping(value = "/testTcc")
    public String getTestTcc(){
        return consumerTccService.testTcc();
    }




}
