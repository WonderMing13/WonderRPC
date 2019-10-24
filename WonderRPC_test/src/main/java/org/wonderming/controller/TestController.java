package org.wonderming.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wonderming.service.WonderService;

/**
 * @author wangdeming
 * @date 2019-10-24 13:28
 **/
@RestController
public class TestController {

    @Autowired
    private WonderService wonderService;

    @GetMapping(value = "/test")
    public String test(){
        return wonderService.getTest("test");
    }
}
