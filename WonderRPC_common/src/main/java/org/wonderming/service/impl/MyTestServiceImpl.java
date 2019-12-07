package org.wonderming.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wonderming.annotation.TccTransaction;
import org.wonderming.service.api.ITestService;
import org.wonderming.service.MyOkService;
import org.wonderming.service.MyTestService;
import org.wonderming.service.api.IWonderService;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.entity.Transaction;
import org.wonderming.tcc.entity.TransactionContext;
import org.wonderming.tcc.entity.TransactionXid;

import javax.annotation.Resource;

/**
 * @author wangdeming
 * @date 2019-11-24 12:30
 **/
@Service
public class MyTestServiceImpl implements MyTestService {

    @Resource
    private TransactionConfiguration transactionConfiguration;

    @Resource
    private ITestService iTestService;

    @Resource
    private IWonderService iWonderService;

    @Autowired
    private MyOkService myOkService;

    @Override
    @TccTransaction(confirmMethod = "test1",cancelMethod = "test2")
    public String test(){
        System.out.println("开始处理逻辑");
        final Transaction currentTransaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
        final String hjp = iTestService.getTest(new TransactionContext(currentTransaction.getXid(),currentTransaction.getStatus()), "HJP");
        final String xjx = iWonderService.getWonder(new TransactionContext(currentTransaction.getXid(), currentTransaction.getStatus()), "XJX");
        System.out.println("处理完成逻辑");
        return hjp + xjx;
    }

    public String test1(){
        System.out.println("confirm方法");
        return "ok";
    }

    public String test2(){
        System.out.println("cancel方法");
        return "ok";
    }


}
