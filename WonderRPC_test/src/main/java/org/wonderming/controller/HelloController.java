package org.wonderming.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wonderming.service.WonderService;

import javax.annotation.Resource;

/**
 * @className: TestController
 * @package: org.wonderming.controller
 * @author: wangdeming
 * @date: 2019-09-11 10:08
 **/
@RestController
public class HelloController {

    @Autowired
    private WonderService wonderService;

    @GetMapping(value = "/wonder")
    public void test(){
        wonderService.testWonder();
    }
}
