package org.wonderming.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wonderming.service.ITestService;

import javax.annotation.Resource;


/**
 * @author wangdeming
 * @date 2019-09-06 15:16
 **/
@RestController
public class TestController {

    @Resource
    private ITestService testService;


    @GetMapping(value = "/hello")
    public String sayTest(){
        return testService.getTest("CS");
    }

}
