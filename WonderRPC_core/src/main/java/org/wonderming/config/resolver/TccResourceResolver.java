package org.wonderming.config.resolver;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author wangdeming
 * @date 2019-11-21 16:13
 **/
@Aspect
@Component
public class TccResourceResolver implements Ordered {

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 2;
    }

    @Pointcut("@annotation(org.wonderming.annotation.TccTransaction)")
    public void tccResource(){}


    @Around("tccResource()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
