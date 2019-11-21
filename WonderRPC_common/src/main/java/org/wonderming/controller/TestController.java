package org.wonderming.controller;

import org.springframework.web.bind.annotation.*;
import org.wonderming.entity.DefaultFuture;
import org.wonderming.entity.RpcFuture;
import org.wonderming.entity.RpcResponse;
import org.wonderming.service.ITestService;
import org.wonderming.service.IWonderService;

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

    @Resource
    private IWonderService wonderService;

    @GetMapping(value = "/hello")
    public void sayTest() throws TimeoutException {
        final RpcFuture<RpcResponse> rpcFuture = testService.getTest("SQL");
        final String wonderStr = wonderService.getWonder("YM");
        System.out.println((String) rpcFuture.get(3000).getResult());
        System.out.println(wonderStr);
    }
}
