package org.wonderming.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wonderming.annotation.ZookeeperLock;
import org.wonderming.service.WonderService;

/**
 * @author wangdeming
 * @date 2019-10-24 13:28
 **/
@Slf4j
@RestController
public class TestController {

    @Autowired
    private WonderService wonderService;

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
