package org.wonderming.controller;

import org.springframework.web.bind.annotation.*;
import org.wonderming.entity.DefaultFuture;
import org.wonderming.entity.RpcFuture;
import org.wonderming.entity.RpcResponse;
import org.wonderming.service.ITestService;

import javax.annotation.Resource;
import java.util.concurrent.TimeoutException;


/**
 * @author wangdeming
 * @date 2019-09-06 15:16
 **/
@RestController
public class TestController {

    @Resource
    private ITestService testService;

    @GetMapping(value = "/hello")
    public String sayTest() throws TimeoutException {
        final RpcFuture<RpcResponse> rpcFuture = testService.getTest("SQL");
        return (String) rpcFuture.get(3000).getResult();
    }
}
