package org.wonderming.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wonderming.service.MyTestService;

import java.util.concurrent.TimeoutException;


/**
 * @author wangdeming
 * @date 2019-09-06 15:16
 **/
@RestController
public class TestController {

    @Autowired
    private MyTestService myTestService;


    @GetMapping(value = "/hello")
    public String sayTest() throws TimeoutException {
        return myTestService.test();
    }



}
