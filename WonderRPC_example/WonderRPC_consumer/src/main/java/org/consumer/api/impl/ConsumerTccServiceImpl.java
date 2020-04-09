package org.consumer.api.impl;

import org.consumer.api.ConsumerOneTestService;
import org.consumer.api.service.ConsumerTccService;
import org.consumer.api.ConsumerTestService;
import org.springframework.stereotype.Service;
import org.wonderming.annotation.TccTransaction;
import org.wonderming.utils.MethodUtil;

import javax.annotation.Resource;

/**
 * @author wangdeming
 **/
@Service
public class ConsumerTccServiceImpl implements ConsumerTccService {

    @Resource
    private ConsumerTestService consumerTestService;

    @Resource
    private ConsumerOneTestService consumerOneTestService;

    @Override
    @TccTransaction(confirmMethod = "confirmTcc",cancelMethod = "cancelTcc")
    public String testTcc() {
        System.out.println("开始执行");
        final String testStr = consumerTestService.test(MethodUtil.getConsumerTransactionContext());
        final String helloStr = consumerOneTestService.test(MethodUtil.getConsumerTransactionContext());
        System.out.println("结束执行");
        return testStr + helloStr;
    }

    public void confirmTcc(){
        System.out.println("根事务的TCC执行成功");
    }

    public String cancelTcc(){
        System.out.println("根事务的TCC执行失败");
        return "ok";
    }
}
