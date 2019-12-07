package org.wonderming.service;

import org.springframework.stereotype.Service;
import org.wonderming.annotation.TccTransaction;
import org.wonderming.tcc.entity.TransactionContext;
import org.wonderming.tcc.type.MethodType;

/**
 * @author wangdeming
 * @date 2019-11-16 16:01
 **/
@Service
public class TestServiceImpl implements TestService {

    @Override
    @TccTransaction(confirmMethod = "ok",cancelMethod = "fuck",type = MethodType.PROVIDER)
    public String getWonder(TransactionContext transactionContext,String str) {
        System.out.println("开始执行逻辑");
        return "hi" + str;
    }

    public String ok(TransactionContext transactionContext,String str){
        return "okConfirm";
    }

    public String fuck(TransactionContext transactionContext,String str){
        return "okCancel";
    }
}
