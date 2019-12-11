package org.wonderming.config.recover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.wonderming.tcc.tccimpl.DefaultTransactionRecovery;

import javax.annotation.PostConstruct;

/**
 * @author wangdeming
 * @date 2019-12-09 16:07
 **/
@AutoConfigureAfter(DefaultTransactionRecovery.class)
public class TccTransactionRecovery {

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private DefaultTransactionRecovery defaultTransactionRecovery;

    @PostConstruct
    public void init(){
        taskScheduler.schedule(()-> defaultTransactionRecovery.startRecover(),new CronTrigger(defaultTransactionRecovery.getTccProperties().getCronExpression()));
    }


}
