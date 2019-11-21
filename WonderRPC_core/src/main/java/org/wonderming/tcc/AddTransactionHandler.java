package org.wonderming.tcc;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * 添加参与事务handler
 * @author wangdeming
 * @date 2019-11-18 22:07
 **/
public interface AddTransactionHandler {
    /**
     * 处理添加事务处理者的逻辑
     * @param proceedingJoinPoint 切点
     * @param method 方法对象
     */
    void process(ProceedingJoinPoint proceedingJoinPoint, Method method);
}
