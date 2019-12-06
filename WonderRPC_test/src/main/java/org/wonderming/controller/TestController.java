package org.wonderming.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wonderming.annotation.ZookeeperLock;
import org.wonderming.service.WonderService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangdeming
 * @date 2019-10-24 13:28
 **/
@Slf4j
@RestController
public class TestController{

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    private WonderService wonderService;

    @GetMapping(value = "/firstTest")
    @ZookeeperLock
    public int testFirst(){
        return wonderService.getZookeeperLock();
    }

    @GetMapping(value = "/sendDirect")
    public void sendDirect(){
        Map<String, Object> map = new HashMap<>(2);
        map.put("msg","点对点消息");
        map.put("data", "helloWorld");
        rabbitTemplate.convertAndSend("exchange.direct","direct.queue", map);
    }

    @GetMapping(value = "/receiveDirect")
    public void receiveDirect(){
        final Object o = rabbitTemplate.receiveAndConvert("direct.queue");
        System.out.println(o);
    }

    @GetMapping(value = "/test")
    public int test(){
        int a = 0;
        try {
            log.info("i am resource 1....");
            a = wonderService.getZookeeperLock();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            log.info("release success...");
        }
        return a;
    }

    @GetMapping(value = "/testOne")
    public int testOne(){
        int b = 0;
        try {
            log.info("i am resource 2.....");
            b = wonderService.getZookeeperLock();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            log.info("release success....");
        }
        return b;
    }

    @GetMapping(value = "/testThree")
    public int testThree(){
        int c = 0;
        try {
            log.info("i am resource 3......");
            c = wonderService.getZookeeperLock();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            log.info("release success....");
        }
        return c;
    }
}
