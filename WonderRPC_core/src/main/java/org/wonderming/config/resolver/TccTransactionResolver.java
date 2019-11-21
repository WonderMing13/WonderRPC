package org.wonderming.config.resolver;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 优先级其次
 * @author wangdeming
 * @date 2019-11-20 16:38
 **/
@Aspect
@Component
public class TccTransactionResolver implements Ordered {

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1;
    }

    @Pointcut("@annotation(org.wonderming.annotation.TccTransaction)")
    public void tccTransaction(){}

    @Around("tccTransaction()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

}
