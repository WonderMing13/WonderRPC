package org.wonderming.service;

import org.springframework.stereotype.Service;
import org.wonderming.annotation.TccTransaction;
import org.wonderming.annotation.ZookeeperLock;
import org.wonderming.tcc.entity.TransactionContext;
import org.wonderming.tcc.type.MethodType;

/**
 * @className: WonderServiceImpl
 * @package: org.wonderming.service
 * @author: wangdeming
 * @date: 2019-09-12 11:16
 **/
@Service
public class WonderServiceImpl implements WonderService {

    private static int i = 0;

    @Override
    @TccTransaction(confirmMethod = "wonder1",cancelMethod = "wonder2",type = MethodType.PROVIDER)
    public String getTest(TransactionContext transactionContext,String str) {
        System.out.println("开始分支TRY事务");
        return "hi" + str;
    }

    public String wonder1(TransactionContext transactionContext,String str){
        System.out.println("transactionContext："+transactionContext);
        System.out.println("str："+str);
        return "okConfirm";
    }

    public String wonder2(TransactionContext transactionContext,String str){
        System.out.println("transactionContext："+transactionContext);
        System.out.println("str："+str);
        return "okCancel";
    }

    @Override
    public int getZookeeperLock() {
        return i++;
    }

}
