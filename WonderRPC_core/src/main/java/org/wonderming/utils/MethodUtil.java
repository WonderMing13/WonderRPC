package org.wonderming.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.wonderming.annotation.TccTransaction;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.entity.Transaction;
import org.wonderming.tcc.entity.TransactionContext;
import org.wonderming.tcc.type.MethodType;
import org.wonderming.tcc.type.PropagationType;

import java.lang.reflect.Method;

/**
 * @author wangdeming
 * @date 2019-11-25 11:06
 **/
public class MethodUtil {

    /**
     * 从参数获取事务上下文
     * @param args 切点参数
     * @return TransactionContext
     */
    public static TransactionContext getTransactionContext(Object[] args){
        TransactionContext context = null;
        for (Object obj:args) {
            if (obj instanceof TransactionContext){
                context = (TransactionContext) obj;
            }
        }
        return context;
    }

    public static MethodType getMethodType(TransactionContext transactionContext,TccTransaction tccTransaction) {
        if (transactionContext == null && tccTransaction.type() == MethodType.ROOT) {
            //事务传播上下文为空且存在TccTransaction注解则为ROOT根事务
            return MethodType.ROOT;
        } else if (transactionContext != null && tccTransaction.type() == MethodType.PROVIDER) {
            //事务传播上下文不为空且存在TccTransaction注解则为PROVIDER事务
            return MethodType.PROVIDER;
        } else {
            return MethodType.NORMAL;
        }
    }

    public static TransactionContext getConsumerTransactionContext(){
        final TransactionConfiguration transactionConfiguration = ApplicationContextUtil.getApplicationContext().getBean(TransactionConfiguration.class);
        final Transaction currentTransaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
        return new TransactionContext(currentTransaction.getXid(),currentTransaction.getStatus());
    }



}
