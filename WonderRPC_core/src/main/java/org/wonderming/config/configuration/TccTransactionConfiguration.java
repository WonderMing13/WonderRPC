package org.wonderming.config.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.tccimpl.DefaultResourceManager;
import org.wonderming.tcc.tccimpl.DefaultTransactionConfiguration;
import org.wonderming.tcc.tccimpl.DefaultTransactionManager;

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

}
