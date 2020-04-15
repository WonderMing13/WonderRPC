package org.provider.service.impl;

import org.provider.service.ProviderOneTestService;
import org.springframework.stereotype.Service;
import org.wonderming.annotation.TccTransaction;
import org.wonderming.tcc.entity.TransactionContext;
import org.wonderming.tcc.type.MethodType;

/**
 * @author wangdeming
 **/
@Service
public class ProviderOneTestServiceImpl implements ProviderOneTestService {

    @Override
    @TccTransaction(confirmMethod = "confirmTest",cancelMethod = "cancelTest",type = MethodType.PROVIDER)
    public String test(TransactionContext transactionContext) {
        System.out.println("xx");
        return "xjx";
    }

    public void confirmTest(TransactionContext transactionContext){
        System.out.println("执行事务Test确认方法");
        throw new RuntimeException("错误");
    }

    public String cancelTest(TransactionContext transactionContext){
        System.out.println("执行事务Test取消方法");
        return "ok";
    }
}
