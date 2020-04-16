package org.consumer.api.impl;

import org.consumer.api.ConsumerOneTestService;
import org.consumer.api.service.ConsumerTccService;
import org.consumer.api.ConsumerTestService;
import org.consumer.api.service.IMerchantInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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

    @Resource
    private IMerchantInfoService merchantInfoService;

    /**
     * 测试TCC分布式事务
     */
    @Override
    @TccTransaction(confirmMethod = "confirmTcc",cancelMethod = "cancelTcc")
    public String testTcc() {
        System.out.println("开始执行");
        final String testStr = consumerTestService.test(MethodUtil.getConsumerTransactionContext());
        final String oneTestStr = consumerOneTestService.test(MethodUtil.getConsumerTransactionContext());
        System.out.println("结束执行");
        return oneTestStr + testStr;
    }

    public void confirmTcc(){
        System.out.println("根事务的TCC执行成功");
//        throw new RuntimeException("error");
    }

    public String cancelTcc(){
        System.out.println("根事务的TCC执行失败");
        return "ok";
    }

    /**
     * 测试本地Spring事务的传播属性
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void testTransaction() {
        merchantInfoService.test();
        merchantInfoService.testWithException();
    }
}
