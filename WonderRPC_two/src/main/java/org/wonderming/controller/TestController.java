package org.wonderming.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wonderming.service.TestService;

import javax.annotation.Resource;

/**
 * @author wangdeming
 * @date 2019-11-16 16:20
 **/
@RestController
public class TestController {

    @Resource
    private TestService testService;

//    @GetMapping(value = "/hi")
//    public String getWonder(){
//        return testService.getWonder("ym");
//    }
}
