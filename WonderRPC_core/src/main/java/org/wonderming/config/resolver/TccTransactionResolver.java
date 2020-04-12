package org.wonderming.config.resolver;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.wonderming.annotation.TccTransaction;
import org.wonderming.config.configuration.ServiceConfiguration;
import org.wonderming.exception.TccTransactionException;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.TransactionManager;
import org.wonderming.tcc.entity.Participant;
import org.wonderming.tcc.entity.Transaction;
import org.wonderming.tcc.entity.TransactionContext;
import org.wonderming.tcc.entity.TransactionXid;
import org.wonderming.tcc.type.MethodType;
import org.wonderming.tcc.type.TransactionStatus;
import org.wonderming.tcc.type.TransactionType;
import org.wonderming.utils.ApplicationContextUtil;
import org.wonderming.utils.AssertUtil;
import org.wonderming.utils.MethodUtil;
import org.wonderming.utils.SnowflakeIdWorkerUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 优先级其次
 *
 * @author wangdeming
 * @date 2019-11-20 16:38
 **/
@Slf4j
@Aspect
public class TccTransactionResolver implements Ordered {

    private final TransactionConfiguration transactionConfiguration;

    @Autowired
    public TccTransactionResolver(TransactionConfiguration transactionConfiguration) {
        this.transactionConfiguration = transactionConfiguration;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1;
    }

    @Pointcut("@annotation(org.wonderming.annotation.TccTransaction)")
    public void tccTransactionAspect() {
    }

    /**
     * 获取事务上下文其次判断是根事务还是分支事务
     *
     * @param joinPoint ProceedingJoinPoint 切点
     * @return Object
     * @throws Throwable 异常
     */
    @Around("tccTransactionAspect() && @annotation(tccTransaction)")
    public Object around(ProceedingJoinPoint joinPoint,TccTransaction tccTransaction) throws Throwable {
        log.info("Starting invoke Tcc Root Transaction...");
        final TransactionContext transactionContext = MethodUtil.getTransactionContext(joinPoint.getArgs());
        //根据事务传播上下文和TccTransaction注解,消费者不作处理(不然Try成功之后删除上下文导致根事务错误)
        final MethodType methodType = MethodUtil.getMethodType(transactionContext,tccTransaction);
        switch (methodType) {
            case ROOT:
                return this.rootProcess(joinPoint);
            case PROVIDER:
                return this.providerProcess(joinPoint,transactionContext);
            default:
                return joinPoint.proceed();
        }
    }

    /**
     * TccTransactionResolver与TccResourceResolver轮流执行
     * <p>
     *     1.开始Root开始事务传播上下文的Try
     *     2.出错就ROLLBACK事务
     *     3.Try正常就提交事务
     * </p>
     * @param joinPoint ProceedingJoinPoint
     * @return Object
     */
    private Object rootProcess(ProceedingJoinPoint joinPoint) {
        final TransactionManager transactionManager = transactionConfiguration.getTransactionManager();
        transactionManager.begin();
        Object obj = null;
        try {
            obj = joinPoint.proceed();
        } catch (Throwable throwable) {
            //Root Try事务发生异常时
            final Transaction transaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
            transactionManager.rollback(transaction);
            throwable.printStackTrace();
            return obj;
        }
        //Root Try事务正常执行时,Provider Try事务异常时
        final Transaction transaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
        final ServiceConfiguration serviceConfiguration = ApplicationContextUtil.getApplicationContext().getBean(ServiceConfiguration.class);
        final String str = serviceConfiguration.findRootId(new String(transaction.getXid().getGlobalTransactionId())).get(0);
        String rootBranchPath = String.format("%s/%s/%s/%s","/tcc","root",new String(transaction.getXid().getGlobalTransactionId()), str);
        final Transaction transactionRoot = serviceConfiguration.findByPath(rootBranchPath);
        System.out.println(transactionRoot);
        if (transactionRoot != null){
            if (transactionRoot.getError() != null){
                transactionManager.rollback(transactionRoot);
            }else {
                transactionManager.commit(transactionRoot);
            }
        }
        return obj;
    }


    private Object providerProcess(ProceedingJoinPoint joinPoint,TransactionContext transactionContext) {
        //TRY阶段
        TransactionContext transactionContextBranch = new TransactionContext(new TransactionXid(transactionContext.getXid().getGlobalTransactionId(), SnowflakeIdWorkerUtil.newId()), transactionContext.getStatus());
        boolean isTry = transactionContextBranch.getStatus() == TransactionStatus.TRY;
        AssertUtil.isTrue(isTry);
        transactionConfiguration.getTransactionManager().begin(transactionContextBranch);
        Object obj = null;
        try {
            obj =  joinPoint.proceed();
        } catch (Throwable throwable) {
            //更新分支错误异常 防止抛出异常导致后面的无法aop拦截
            final Transaction transaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
            transaction.setError(throwable);
            final int update = transactionConfiguration.getResourceManager().update(transaction);
            log.info("update Branch error transaction {},result:{}",transaction,update);
            //更新主分支错误异常
            final ServiceConfiguration serviceConfiguration = ApplicationContextUtil.getApplicationContext().getBean(ServiceConfiguration.class);
            final String str = serviceConfiguration.findRootId(new String(transaction.getXid().getGlobalTransactionId())).get(0);
            String rootBranchPath = String.format("%s/%s/%s/%s","/tcc","root",new String(transaction.getXid().getGlobalTransactionId()), str);
            final Transaction transactionRoot = serviceConfiguration.findByPath(rootBranchPath);
            transactionRoot.setError(throwable);
            final int updateRoot = serviceConfiguration.updateRoot(transactionRoot);
            log.info("update Root error transaction {},result:{}",transactionRoot,updateRoot);
            throwable.printStackTrace();
        }
        Method method = ((MethodSignature) (joinPoint.getSignature())).getMethod();
        System.out.println(method.getReturnType());
        return obj;
    }
}
