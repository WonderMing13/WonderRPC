package org.wonderming.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wonderming.entity.HelloService;

/**
 * @className: TestController
 * @package: org.wonderming.controller
 * @author: wangdeming
 * @date: 2019-09-06 15:16
 **/
@RestController
public class TestController {

    @Autowired
    private HelloService helloService;

    @RequestMapping(value = "/test")
    public String sayHello(){
        return helloService.sayHello();
    }

}
