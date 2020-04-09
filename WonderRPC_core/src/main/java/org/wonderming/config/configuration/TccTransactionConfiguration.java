package org.wonderming.config.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.wonderming.config.recover.TccTransactionRecovery;
import org.wonderming.config.resolver.TccResourceResolver;
import org.wonderming.config.resolver.TccTransactionResolver;
import org.wonderming.config.thread.MyThreadFactory;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.entity.InvocationContext;
import org.wonderming.tcc.tccimpl.DefaultResourceManager;
import org.wonderming.tcc.tccimpl.DefaultTransactionConfiguration;
import org.wonderming.tcc.tccimpl.DefaultTransactionManager;
import org.wonderming.tcc.tccimpl.DefaultTransactionRecovery;
import org.wonderming.utils.ApplicationContextUtil;
import org.wonderming.utils.MethodUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author wangdeming
 * @date 2019-11-21 17:14
 **/
@Configuration
public class TccTransactionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TransactionConfiguration getConfiguration(){
        final DefaultTransactionConfiguration df = new DefaultTransactionConfiguration();
        df.setResourceManager(new DefaultResourceManager());
        df.setTransactionManager(new DefaultTransactionManager(df));
        return df;
    }

    @Bean
    public TccTransactionResolver tccTransactionResolver(){
        return new TccTransactionResolver(getConfiguration());
    }

    @Bean
    public TccResourceResolver tccResourceResolver(){
        return new TccResourceResolver(getConfiguration());
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultTransactionRecovery defaultTransactionRecovery(){
        final DefaultTransactionRecovery defaultTransactionRecovery = new DefaultTransactionRecovery();
        defaultTransactionRecovery.setTransactionConfiguration(getConfiguration());
        return defaultTransactionRecovery;
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(){
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public TccTransactionRecovery tccTransactionRecovery(){
        return new TccTransactionRecovery();
    }

}
