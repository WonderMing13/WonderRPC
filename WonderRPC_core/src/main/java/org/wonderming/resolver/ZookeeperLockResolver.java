package org.wonderming.resolver;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.wonderming.config.configuration.ServiceConfiguration;

import java.util.concurrent.TimeUnit;

/**
 * @author wangdeming
 * @date 2019-11-07 17:35
 **/
@Aspect
@Component
public class ZookeeperLockResolver {

    @Pointcut("@annotation(org.wonderming.annotation.ZookeeperLock)")
    public void zookeeperLock(){}

    @Around("zookeeperLock()")
    public Object around(ProceedingJoinPoint joinPoint) throws Exception {
        Object obj = null;
        boolean isLocked = false;
        final InterProcessMutex interProcessMutex = ServiceConfiguration.getInterProcessMutex();
        try {
            //阻塞2秒获取锁
            isLocked = interProcessMutex.acquire(10, TimeUnit.SECONDS);
            if (isLocked){
                //获取锁成功
                obj = joinPoint.proceed();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }finally {
            //释放锁
            if (isLocked){
                interProcessMutex.release();
            }
        }
        return obj;
    }
}