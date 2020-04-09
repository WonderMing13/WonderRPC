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
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.entity.*;
import org.wonderming.tcc.type.MethodType;
import org.wonderming.tcc.type.TransactionStatus;
import org.wonderming.utils.MethodUtil;
import org.wonderming.utils.SnowflakeIdWorkerUtil;

import java.lang.reflect.Method;

/**
 * @author wangdeming
 * @date 2019-11-21 16:13
 **/
@Slf4j
@Aspect
public class TccResourceResolver implements Ordered {

    private final TransactionConfiguration transactionConfiguration;

    @Autowired
    public TccResourceResolver(TransactionConfiguration transactionConfiguration) {
        this.transactionConfiguration = transactionConfiguration;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 2;
    }

    @Pointcut("@annotation(org.wonderming.annotation.TccTransaction)")
    public void tccResourceAspect() {
    }

    @SuppressWarnings("all")
    @Around("tccResourceAspect() && @annotation(tccTransaction)")
    public Object around(ProceedingJoinPoint joinPoint, TccTransaction tccTransaction) throws Throwable {
        log.info("Tcc Resource 拦截成功....");
        final Transaction currentTransaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
        //当前事务传播上下文不为空且状态为TRY阶段 也就是ROOT事务的TRY阶段已经设置成功
        if (currentTransaction != null && currentTransaction.getStatus().equals(TransactionStatus.TRY)) {
            final TransactionContext transactionContext = MethodUtil.getTransactionContext(joinPoint.getArgs());
            final MethodType methodType = MethodUtil.getMethodType(transactionContext, tccTransaction);
            log.info("Method Type:" + methodType);
            switch (methodType) {
                case ROOT:
                    this.generateRootParticipant(joinPoint,tccTransaction);
                    break;
                case PROVIDER:
                    this.generateBranchParticipant(joinPoint,tccTransaction);
                    break;
                default:
                    break;
            }
        }
        return joinPoint.proceed();
    }

    private void generateBranchParticipant(ProceedingJoinPoint joinPoint,TccTransaction tccTransaction){
        final Transaction currentTransaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
        final TransactionXid xid = new TransactionXid(currentTransaction.getXid().getGlobalTransactionId(), SnowflakeIdWorkerUtil.newId());
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Method method = methodSignature.getMethod();
        //确认方法名称
        final String confirmMethod = tccTransaction.confirmMethod();
        //取消方法名称
        final String cancelMethod = tccTransaction.cancelMethod();
        //Try方法中切面拦截的参数
        final Object[] tryArgs = joinPoint.getArgs();
        //确认方法参数
        Object[] confirmArgs = new Object[tryArgs.length];
        //取消方法参数
        Object[] cancelArgs = new Object[tryArgs.length];
        //Try方法中的参数复制到确认方法的参数中
        System.arraycopy(tryArgs,0,confirmArgs,0,tryArgs.length);
        //默认第一个参数为事务传播上下文
        confirmArgs[0] = new TransactionContext(xid,TransactionStatus.CONFIRM);
        //Try方法中的参数复制到取消方法的参数中
        System.arraycopy(tryArgs,0,cancelArgs,0,tryArgs.length);
        //默认第一个参数为事务传播上下文
        cancelArgs[0] = new TransactionContext(xid,TransactionStatus.CANCEL);
        final Class<?> aClass = joinPoint.getTarget().getClass();
        //确认方法最小执行体
        final InvocationContext confirmInvocation = new InvocationContext();
        confirmInvocation.setTargetClassName(aClass.getName())
                         .setParameterTypes(method.getParameterTypes())
                         .setParam(confirmArgs)
                         .setMethodName(confirmMethod);
        //取消方法最小执行体
        final InvocationContext cancelInvocation = new InvocationContext();
        cancelInvocation.setTargetClassName(aClass.getName())
                        .setParameterTypes(method.getParameterTypes())
                        .setParam(cancelArgs)
                        .setMethodName(cancelMethod);
        currentTransaction.addParticipant(new Participant(xid, confirmInvocation, cancelInvocation));
        final int update = transactionConfiguration.getResourceManager().update(currentTransaction);
        log.info("update Branch Participant transaction {},result:{}",currentTransaction,update);
    }


    private void generateRootParticipant(ProceedingJoinPoint joinPoint,TccTransaction tccTransaction) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Method method = methodSignature.getMethod();
        //根事务确认方法
        final String confirmMethod = tccTransaction.confirmMethod();
        //根事务取消方法
        final String cancelMethod = tccTransaction.cancelMethod();
        final Transaction currentTransaction = transactionConfiguration.getTransactionManager().getCurrentTransaction();
        final TransactionXid transactionXid = new TransactionXid(currentTransaction.getXid().getGlobalTransactionId(), SnowflakeIdWorkerUtil.newId());
        //注解拦截到的类,确认方法和取消方法直接和提供者所在的类一起
        final Class<?> aClass = joinPoint.getTarget().getClass();
        //确认方法的最小执行体
        final InvocationContext confirmInvocation = new InvocationContext();
        confirmInvocation.setTargetClassName(aClass.getName())
                .setParam(joinPoint.getArgs())
                .setMethodName(confirmMethod)
                .setParameterTypes(method.getParameterTypes());
        //取消方法的最小执行体
        final InvocationContext cancelInvocation = new InvocationContext();
        cancelInvocation.setTargetClassName(aClass.getName())
                .setParam(joinPoint.getArgs())
                .setMethodName(cancelMethod)
                .setParameterTypes(method.getParameterTypes());
        final Participant participant = new Participant(transactionXid, confirmInvocation, cancelInvocation);
        currentTransaction.addParticipant(participant);
        final int update = transactionConfiguration.getResourceManager().update(currentTransaction);
        log.info("update Root Participant transaction {},result:{}",currentTransaction,update);
    }


}
