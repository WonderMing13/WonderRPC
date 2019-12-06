package org.wonderming.config.resolver;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.wonderming.annotation.TccTransaction;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.TransactionManager;
import org.wonderming.tcc.entity.Participant;
import org.wonderming.tcc.entity.Transaction;
import org.wonderming.tcc.entity.TransactionContext;
import org.wonderming.tcc.entity.TransactionXid;
import org.wonderming.tcc.type.MethodType;
import org.wonderming.tcc.type.TransactionStatus;
import org.wonderming.utils.AssertUtil;
import org.wonderming.utils.MethodUtil;
import org.wonderming.utils.SnowflakeIdWorkerUtil;

import java.util.List;

/**
 * 优先级其次
 *
 * @author wangdeming
 * @date 2019-11-20 16:38
 **/
@Slf4j
@Aspect
@Component
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
            final Transaction transaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
            transactionManager.rollback(transaction);
            throwable.printStackTrace();
        }
        final Transaction transaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
        transactionManager.commit(transaction);
        return obj;
    }


    private Object providerProcess(ProceedingJoinPoint joinPoint,TransactionContext transactionContext) throws Throwable {
        TransactionContext transactionContextBranch = new TransactionContext(new TransactionXid(transactionContext.getXid().getGlobalTransactionId(), SnowflakeIdWorkerUtil.newId()), transactionContext.getStatus());
        boolean isTry = transactionContextBranch.getStatus() == TransactionStatus.TRY;
        AssertUtil.isTrue(isTry);
        transactionConfiguration.getTransactionManager().begin(transactionContextBranch);
        return joinPoint.proceed();
    }
}
