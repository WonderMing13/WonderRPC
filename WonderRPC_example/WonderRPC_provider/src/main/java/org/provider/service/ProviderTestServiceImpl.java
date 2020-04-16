package org.provider.service;

import org.springframework.stereotype.Service;
import org.wonderming.annotation.TccTransaction;
import org.wonderming.tcc.entity.TransactionContext;
import org.wonderming.tcc.type.MethodType;

/**
 * @author wangdeming
 **/
@Service
public class ProviderTestServiceImpl implements ProviderTestService {

    @Override
    @TccTransaction(confirmMethod = "confirmHello",cancelMethod = "cancelHello",type = MethodType.PROVIDER)
    public String test(TransactionContext transactionContext) {
        throw new RuntimeException("error");
    }

    public void confirmHello(TransactionContext transactionContext){
        System.out.println("执行事务Hello确认方法");
    }

    public String cancelHello(TransactionContext transactionContext){
        System.out.println("执行事务Hello取消方法");
        return "ok";
    }
}
